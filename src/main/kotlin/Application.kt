package su.kawunprint

import io.ktor.server.application.*
import su.kawunprint.plugins.*
import su.kawunprint.plugins.Databases.initDatabase

fun main(args: Array<String>) {
}

fun Application.module() {
    initDatabase()
    configureDI()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureMonitoring()
}
