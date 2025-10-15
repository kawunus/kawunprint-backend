package su.kawunprint

import io.ktor.server.application.Application
import io.ktor.server.application.install
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