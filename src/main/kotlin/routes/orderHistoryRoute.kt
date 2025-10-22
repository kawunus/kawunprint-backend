package routes

import data.model.OrderHistoryModel
import data.model.requests.order.history.CreateOrderHistoryRequest
import domain.usecase.OrderHistoryUseCase
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

fun Route.orderHistoryRoute() {
    val orderHistoryUseCase: OrderHistoryUseCase by inject()
    val orderUseCase: OrderUseCase by inject()
    val userUseCase: UserUseCase by inject()
    authenticate("jwt") {
        route("/{orderId}/history") {
            get {
                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val principal = call.principal<JWTPrincipal>()!!
                val userRole = RoleModel.valueOf(principal.payload.getClaim("role").asString())
                val userId = principal.payload.getClaim("id").asInt()

                val order = orderUseCase.getOrderById(orderId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Order not found")

                if (userRole == RoleModel.CLIENT && order.customer.id != userId) {
                    return@get call.respond(HttpStatusCode.Forbidden)
                }

                val history = orderHistoryUseCase.getHistoryForOrder(orderId)
                call.respond(HttpStatusCode.OK, history)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                val principal = call.principal<JWTPrincipal>()!!
                val employeeId = principal.payload.getClaim("id").asInt()
                val employee =
                    userUseCase.getUserById(employeeId) ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val order = orderUseCase.getOrderById(orderId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Order not found")

                val body = call.receive<CreateOrderHistoryRequest>()

                val historyEntry = OrderHistoryModel(
                    orderId = orderId,
                    employee = employee,
                    status = body.status,
                    comment = body.comment,
                    createdAt = LocalDateTime.now()
                )

                val createdEntry = orderHistoryUseCase.addHistoryEntry(historyEntry)

                if (createdEntry != null && !body.status.isNullOrBlank()) {
                    val isCompleted = body.status.equals("completed", ignoreCase = true)
                    val updatedOrder = order.copy(
                        status = body.status,
                        completedAt = if (isCompleted && order.completedAt == null) LocalDateTime.now() else order.completedAt
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