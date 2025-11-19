package routes

import data.model.OrderStatusModel
import domain.usecase.OrderStatusUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel

fun Route.orderStatusRoute() {
    val orderStatusUseCase: OrderStatusUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/order-status") {

            get {
                val statuses = orderStatusUseCase.getAll()
                call.respond(HttpStatusCode.OK, statuses)
            }

            get("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE, RoleModel.ANALYST)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val status = orderStatusUseCase.getById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, status)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val statusRequest = call.receive<OrderStatusModel>()
                orderStatusUseCase.create(statusRequest)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<OrderStatusModel>()
                orderStatusUseCase.update(request.copy(id = id))
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                orderStatusUseCase.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
