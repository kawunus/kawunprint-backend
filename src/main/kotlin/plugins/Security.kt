package su.kawunprint.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject
import su.kawunprint.domain.usecase.UserUseCase

fun Application.configureSecurity() {
    val userUseCase: UserUseCase by inject()
    authentication {
        jwt("jwt") {
            verifier(userUseCase.getVerifier())
            realm = "ServiceServer"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = userUseCase.getUserByEmail(email)
                user
            }
        }
    }

}
