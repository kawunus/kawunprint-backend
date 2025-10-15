package su.kawunprint.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import su.kawunprint.routes.UserRoute

fun Application.configureRouting() {

    routing {
        UserRoute()
    }
}
