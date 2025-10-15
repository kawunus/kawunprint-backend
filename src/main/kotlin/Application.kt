package su.kawunprint

import io.ktor.server.application.*
import su.kawunprint.DatabaseFactory.initDatabase

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    initDatabase()
    configureDI()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureMonitoring()
}
