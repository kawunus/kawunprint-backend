package routes

import data.model.PrinterModel
import domain.usecase.PrinterUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel

fun Route.printerRoute() {
    val printerUseCase: PrinterUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/printers") {

            get {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val printers = printerUseCase.getAllPrinters()
                call.respond(HttpStatusCode.OK, printers)
            }

            get("/active") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val state = call.request.queryParameters["state"]?.toBooleanStrictOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid 'state' query param")
                val printers = printerUseCase.getPrintersByActiveState(state)
                call.respond(HttpStatusCode.OK, printers)
            }

            get("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val printer = printerUseCase.getPrinterById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, printer)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val printerRequest = call.receive<PrinterModel>()
                printerUseCase.createPrinter(printerRequest)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<PrinterModel>()
                printerUseCase.updatePrinter(request.copy(id = id))
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                printerUseCase.deletePrinter(id)
                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}/active") {
                call.authenticateWithRole(RoleModel.EMPLOYEE, RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val stateParam = call.request.queryParameters["state"]?.toBooleanStrictOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing or invalid 'state' query param")

                printerUseCase.updatePrinterActiveState(id, stateParam)
                call.respond(HttpStatusCode.OK, "Printer #$id active = $stateParam")
            }
        }
    }
}