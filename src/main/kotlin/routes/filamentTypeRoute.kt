package su.kawunprint.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.data.model.RoleModel
import su.kawunprint.data.model.requests.filament.types.CreateFilamentTypeRequest
import su.kawunprint.data.model.requests.filament.types.UpdateFilamentTypeRequest
import su.kawunprint.domain.usecase.FilamentTypeUseCase

fun Route.filamentTypeRoute() {
    val filamentTypeUseCase: FilamentTypeUseCase by inject()
    authenticate("jwt") {
        route("/api/v1/filaments/types") {
            get {
                val filamentTypes = filamentTypeUseCase.getAllFilamentTypes()
                call.respond(HttpStatusCode.OK, filamentTypes)
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val filamentType = filamentTypeUseCase.getFilamentTypeById(id.toInt()) ?: return@get call.respond(
                    HttpStatusCode.NotFound
                )
                call.respond(HttpStatusCode.OK, filamentType)
            }

            post {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val requestBody = call.receive<CreateFilamentTypeRequest>()
                try {
                    val filamentType = FilamentTypeModel(
                        name = requestBody.name,
                        description = requestBody.description
                    )
                    filamentTypeUseCase.createFilamentType(filamentType)
                    call.respond(HttpStatusCode.Created, filamentType)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val type =
                    filamentTypeUseCase.getFilamentTypeById(id) ?: return@delete call.respond(HttpStatusCode.NotFound)

                filamentTypeUseCase.deleteFilamentType(type)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val requestBody = call.receive<UpdateFilamentTypeRequest>()
                try {
                    val newFilamentType = FilamentTypeModel(
                        name = requestBody.name,
                        description = requestBody.description,
                        id = id
                    )
                    filamentTypeUseCase.updateFilamentType(newFilamentType)
                    call.respond(HttpStatusCode.OK, newFilamentType)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}