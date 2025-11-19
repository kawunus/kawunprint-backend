package routes

import data.model.PrinterHistoryModel
import domain.usecase.PrinterHistoryUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel

fun Route.printerHistoryRoute() {
    val printerHistoryUseCase: PrinterHistoryUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/printer-history") {

            get {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE, RoleModel.ANALYST)
                val histories = printerHistoryUseCase.getAll()
                call.respond(HttpStatusCode.OK, histories)
            }

            get("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE, RoleModel.ANALYST)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val history = printerHistoryUseCase.getById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, history)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                call.request.headers["X-User-ID"]?.toIntOrNull()
                val historyRequest = call.receive<PrinterHistoryModel>()
                printerHistoryUseCase.create(historyRequest)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<PrinterHistoryModel>()
                printerHistoryUseCase.update(request.copy(id = id))
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                printerHistoryUseCase.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
