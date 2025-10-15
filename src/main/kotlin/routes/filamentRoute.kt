package su.kawunprint.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.domain.usecase.FilamentUseCase

fun Route.filamentRoute() {
    val filamentUseCase: FilamentUseCase by inject()
    authenticate("jwt") {
        route("/api/v1/filaments") {
            get {
                val filaments = filamentUseCase.getAllFilaments()
                call.respond(HttpStatusCode.OK, filaments)
            }

//            post {
//                call.authenticateWithRole(RoleModel.ADMIN, RoleModel.EMPLOYEE)
//                val body = call.receive<CreateFilamentRequest>()
//                val filament = FilamentModel(
//                    color = body.color,
//                    pricePerGram = body.pricePerGram,
//                    type = ,
//                )
//                filamentUseCase.createFilament(filament)
//            }
        }


    }
}