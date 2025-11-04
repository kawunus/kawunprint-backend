package routes

import data.model.OrderModel
import data.model.requests.order.CreateOrderRequest
import data.model.requests.order.UpdateOrderRequest
import domain.usecase.OrderUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel
import su.kawunprint.domain.usecase.UserUseCase
import java.time.LocalDateTime

fun Route.orderRoute() {
    val orderUseCase: OrderUseCase by inject()
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
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("id").asInt()
                val customer = userUseCase.getUserById(userId) ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val body = call.receive<CreateOrderRequest>()

                val order = OrderModel(
                    customer = customer,
                    employee = null,
                    status = body.status,
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

                val isCompleted = body.status.equals("completed", ignoreCase = true)

                val updatedOrder = existingOrder.copy(
                    employee = employee,
                    status = body.status,
                    totalPrice = body.totalPrice,
                    comment = body.comment,
                    completedAt = if (isCompleted && existingOrder.completedAt == null) LocalDateTime.now() else existingOrder.completedAt
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
        }
    }
}