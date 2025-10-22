package su.kawunprint.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import routes.cartRoute
import routes.printerRoute
import su.kawunprint.routes.filamentRoute
import su.kawunprint.routes.filamentTypeRoute
import su.kawunprint.routes.userRoute

fun Application.configureRouting() {

    routing {
        userRoute()
        filamentRoute()
        filamentTypeRoute()
        printerRoute()
        cartRoute()
    }
}
