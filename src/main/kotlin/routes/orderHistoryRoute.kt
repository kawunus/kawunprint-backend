package routes

import data.model.OrderHistoryModel
import data.model.UserModel
import data.model.requests.order.history.CreateOrderHistoryRequest
import domain.usecase.OrderHistoryUseCase
import domain.usecase.OrderUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel
import su.kawunprint.domain.usecase.UserUseCase
import java.time.LocalDateTime

fun Route.orderHistoryRoute() {
    val orderHistoryUseCase: OrderHistoryUseCase by inject()
    val orderUseCase: OrderUseCase by inject()
    val userUseCase: UserUseCase by inject()
    authenticate("jwt") {
        route("/api/v1/orders/{orderId}/history") {
            get {
                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                orderUseCase.getOrderById(orderId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Order not found")

                val history = orderHistoryUseCase.getHistoryForOrder(orderId)
                call.respond(HttpStatusCode.OK, history)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                // JWT validate returns a UserModel as principal, use that type here
                val principal = call.principal<UserModel>()!!
                val employeeId = principal.id
                val employee =
                    userUseCase.getUserById(employeeId) ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val order = orderUseCase.getOrderById(orderId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Order not found")

                val body = call.receive<CreateOrderHistoryRequest>()

                val historyEntry = OrderHistoryModel(
                    orderId = orderId,
                    employee = employee,
                    statusId = body.statusId,
                    comment = body.comment,
                    createdAt = LocalDateTime.now()
                )

                val createdEntry = orderHistoryUseCase.addHistoryEntry(historyEntry)

                if (createdEntry != null) {
                    val updatedOrder = order.copy(
                        statusId = body.statusId,
                        completedAt = order.completedAt
                    )
                    orderUseCase.updateOrder(updatedOrder)
                }

                if (createdEntry != null) {
                    call.respond(HttpStatusCode.Created, createdEntry)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}