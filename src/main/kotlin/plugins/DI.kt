package su.kawunprint.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import su.kawunprint.di.dataModule
import su.kawunprint.di.domainModule

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(dataModule, domainModule)
    }
}