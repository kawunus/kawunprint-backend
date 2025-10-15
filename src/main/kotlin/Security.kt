package su.kawunprint

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.JwtService
import su.kawunprint.data.repository.UserRepositoryImpl
import su.kawunprint.domain.usecase.UserUseCase

fun Application.configureSecurity() {

    val userUseCase: UserUseCase by inject()

    authentication {
        jwt {
            verifier(userUseCase.getVerifier())
            realm = "ServiceServer"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = userUseCase.getUserByEmail(email)
            }
        }
    }

}
