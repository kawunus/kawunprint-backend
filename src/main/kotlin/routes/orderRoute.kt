package routes

import data.model.OrderModel
import data.model.UserModel
import data.model.requests.order.ConsumeFilamentRequest
import data.model.requests.order.CreateOrderRequest
import data.model.requests.order.UpdateOrderRequest
import data.model.responses.ConsumeFilamentResponse
import data.model.responses.FilamentResponse
import data.model.responses.FilamentTypeSimple
import data.model.responses.OrderResponse
import domain.usecase.OrderUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel
import su.kawunprint.domain.usecase.FilamentUseCase
import su.kawunprint.domain.usecase.UserUseCase
import java.time.LocalDateTime

fun Route.orderRoute() {
    val orderUseCase: OrderUseCase by inject()
    val filamentUseCase: FilamentUseCase by inject()
    val userUseCase: UserUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/orders") {
            get {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val orders = orderUseCase.getAllOrders()
                call.respond(HttpStatusCode.OK, orders)
            }

            get("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val order = orderUseCase.getOrderById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.OK, order)
            }

            post {
                // Allow admin/employee to create an order for a customer specified in body.
                // The X-User-Id header (if present) is treated as the acting employee ID for audit only.
                call.principal<UserModel>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                // employeeId for audit can be present in header
                val employeeId = call.request.headers["X-User-Id"]?.toIntOrNull()

                val body = call.receive<CreateOrderRequest>()
                val customerId = body.customerId
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing customerId in request body")

                val customer = userUseCase.getUserById(customerId)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid customerId")

                val employee = employeeId?.let { userUseCase.getUserById(it) }

                val order = OrderModel(
                    customer = customer,
                    employee = employee,
                    statusId = body.statusId,
                    totalPrice = body.totalPrice,
                    createdAt = LocalDateTime.now(),
                    completedAt = null,
                    comment = body.comment
                )

                val createdOrder = orderUseCase.createOrder(order)
                if (createdOrder != null) {
                    call.respond(HttpStatusCode.Created, createdOrder)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            put("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val existingOrder = orderUseCase.getOrderById(id)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                val body = call.receive<UpdateOrderRequest>()
                val employee = body.employeeId?.let { userUseCase.getUserById(it) }

                if (body.employeeId != null && employee == null) {
                    return@put call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                }

                val updatedOrder = existingOrder.copy(
                    employee = employee,
                    statusId = body.statusId,
                    totalPrice = body.totalPrice,
                    comment = body.comment,
                    completedAt = existingOrder.completedAt
                )

                orderUseCase.updateOrder(updatedOrder)
                call.respond(HttpStatusCode.OK, updatedOrder)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                if (orderUseCase.getOrderById(id) == null) {
                    return@delete call.respond(HttpStatusCode.NotFound)
                }

                orderUseCase.deleteOrder(id)
                call.respond(HttpStatusCode.OK)
            }

            post("/{orderId}/consume-filament") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                val request = call.receive<ConsumeFilamentRequest>()

                val principal = call.principal<UserModel>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val employeeId = principal.id

                try {
                    // Delegate transactional work to data layer through usecase
                    filamentUseCase.consumeFilamentForOrder(
                        orderId,
                        request.filamentId,
                        request.gramsUsed,
                        employeeId,
                        request.comment
                    )

                    // fetch updated models via usecases (separate queries)
                    val updatedOrder = orderUseCase.getOrderById(orderId)
                        ?: return@post call.respond(HttpStatusCode.InternalServerError)
                    val updatedFilament = filamentUseCase.getFilamentById(request.filamentId)
                        ?: return@post call.respond(HttpStatusCode.InternalServerError)

                    // Map to response DTOs with consistent numeric types
                    val orderResp = OrderResponse(
                        id = updatedOrder.id,
                        statusId = updatedOrder.statusId,
                        totalPrice = updatedOrder.totalPrice
                    )

                    val filamentType = updatedFilament.type
                    val filamentResp = FilamentResponse(
                        id = updatedFilament.id,
                        residue = updatedFilament.residue.toDouble(),
                        pricePerGram = updatedFilament.pricePerGram,
                        color = updatedFilament.color,
                        type = FilamentTypeSimple(
                            id = filamentType.id,
                            name = filamentType.name,
                            description = filamentType.description
                        )
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        ConsumeFilamentResponse(success = true, order = orderResp, filament = filamentResp)
                    )
                } catch (e: NoSuchElementException) {
                    if (e.message == "filament_not_found") {
                        return@post call.respond(HttpStatusCode.NotFound, "Filament not found")
                    }
                    if (e.message == "order_not_found") {
                        return@post call.respond(HttpStatusCode.NotFound, "Order not found")
                    }
                    e.printStackTrace()
                    return@post call.respond(HttpStatusCode.InternalServerError)
                } catch (e: IllegalStateException) {
                    // message format: "insufficient:{residue}"
                    val parts = e.message?.split(":") ?: listOf()
                    val residue = parts.getOrNull(1) ?: "0"
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Insufficient filament residue. Available: ${residue}g, requested: ${request.gramsUsed}g"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@post call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}