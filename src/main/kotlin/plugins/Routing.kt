package su.kawunprint.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import routes.cartRoute
import routes.orderHistoryRoute
import routes.orderRoute
import routes.printerRoute
import su.kawunprint.routes.filamentRoute
import su.kawunprint.routes.filamentTypeRoute
import su.kawunprint.routes.userRoute

fun Application.configureRouting() {

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        userRoute()
        filamentRoute()
        filamentTypeRoute()
        printerRoute()
        cartRoute()
        orderRoute()
        orderHistoryRoute()
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5" // версия Swagger UI
        }
    }
}
