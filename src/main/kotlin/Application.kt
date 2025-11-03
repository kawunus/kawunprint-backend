package su.kawunprint

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import su.kawunprint.data.model.responses.BaseResponse
import su.kawunprint.domain.exception.UnauthorizedException
import su.kawunprint.plugins.*
import su.kawunprint.plugins.Databases.initDatabase
import su.kawunprint.utils.Constants

fun main(args: Array<String>) {
}

fun Application.module() {
    initDatabase()
    install(StatusPages) {
        exception<UnauthorizedException> { call, e ->
            call.respond(
                HttpStatusCode.Unauthorized, BaseResponse(false, e.message ?: Constants.ErrorMessages.UNAUTHORIZED)
            )
        }
        exception<Exception> { call, e ->
            call.respond(HttpStatusCode.InternalServerError, BaseResponse(false, Constants.ErrorMessages.GENERAL))
            e.printStackTrace()
        }
    }
    configureDI()
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        allowCredentials = true
    }
    configureSerialization()

    configureSecurity()
    configureRouting()
    configureMonitoring()
}
