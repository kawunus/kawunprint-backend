package su.kawunprint.routes

import data.model.requests.filament.UpdateFilamentRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.RoleModel
import su.kawunprint.data.model.requests.filament.CreateFilamentRequest
import su.kawunprint.domain.usecase.FilamentTypeUseCase
import su.kawunprint.domain.usecase.FilamentUseCase

fun Route.filamentRoute() {
    val filamentUseCase: FilamentUseCase by inject<FilamentUseCase>()
    val filamentTypeUseCase: FilamentTypeUseCase by inject<FilamentTypeUseCase>()

    authenticate("jwt") {
        route("/api/v1/filaments") {

            get {
                val typeIdParam = call.request.queryParameters["typeId"]?.toIntOrNull()
                val filaments = if (typeIdParam != null) {
                    val type = filamentTypeUseCase.getFilamentTypeById(typeIdParam)
                    if (type != null) {
                        filamentUseCase.getFilamentsByType(type)
                    } else emptyList()
                } else {
                    filamentUseCase.getAllFilaments()
                }
                call.respond(HttpStatusCode.OK, filaments)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val filament = filamentUseCase.getAllFilaments().find { it.id == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, filament)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val body = call.receive<CreateFilamentRequest>()
                val filamentType = filamentTypeUseCase.getFilamentTypeById(body.typeId)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid filament type ID")

                val filament = FilamentModel(
                    color = body.color,
                    pricePerGram = body.pricePerGram,
                    type = filamentType,
                    residue = body.residue,
                    hexColor = body.hexColor,
                )

                filamentUseCase.createFilament(filament)
                call.respond(HttpStatusCode.Created, filament)
            }

            put("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val body = call.receive<UpdateFilamentRequest>()
                val filamentType = filamentTypeUseCase.getFilamentTypeById(body.typeId)
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid filament type ID")

                val filament = FilamentModel(
                    id = id,
                    color = body.color,
                    pricePerGram = body.pricePerGram,
                    type = filamentType,
                    residue = body.residue,
                    hexColor = body.hexColor,
                )

                filamentUseCase.updateFilament(filament)
                call.respond(HttpStatusCode.OK, filament)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val filament = filamentUseCase.getAllFilaments().find { it.id == id }
                    ?: return@delete call.respond(HttpStatusCode.NotFound)

                filamentUseCase.deleteFilament(filament)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
