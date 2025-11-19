package su.kawunprint.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import routes.*
import su.kawunprint.routes.filamentRoute
import su.kawunprint.routes.filamentTypeRoute
import su.kawunprint.routes.userRoute

fun Application.configureRouting() {

    routing {
        userRoute()
        filamentRoute()
        filamentTypeRoute()
        printerRoute()
        orderRoute()
        orderHistoryRoute()
        orderStatusRoute()
        printerHistoryRoute()
        orderFileRoute()
        adminFileRoute()

        staticFiles("/uploads", java.io.File("./uploads"))

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5" // версия Swagger UI
        }
    }
}
