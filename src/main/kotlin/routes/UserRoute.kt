package su.kawunprint.routes

import data.model.UserModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.hashPassword
import su.kawunprint.data.model.RoleModel
import su.kawunprint.data.model.requests.LoginRequest
import su.kawunprint.data.model.requests.RegisterRequest
import su.kawunprint.data.model.responses.BaseResponse
import su.kawunprint.domain.usecase.UserUseCase
import su.kawunprint.utils.Constants

fun Route.UserRoute() {
    val userUseCase: UserUseCase by inject()
    val hashFunction = { p: String -> hashPassword(password = p) }

    post("/api/v1/register") {
        val registerRequest = call.receiveNullable<RegisterRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.GENERAL_ERROR))
            return@post
        }

        try {
            val user = UserModel(
                id = 0,
                email = registerRequest.email.trim().lowercase(),
                password = hashFunction(registerRequest.password),
                firstName = registerRequest.firstName.trim(),
                lastName = registerRequest.lastName.trim(),
                phoneNumber = registerRequest.phoneNumber.trim(),
                role = RoleModel.CLIENT,
                isActive = true
            )

            userUseCase.createUser(user)
            val token = userUseCase.generateToken(user)
            call.respond(HttpStatusCode.OK, BaseResponse(true, token))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: Constants.Error.GENERAL_ERROR))
        }
    }

    post("/api/v1/login") {
        val loginRequest = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.GENERAL_ERROR))
            return@post
        }

        try {
            val user = userUseCase.getUserByEmail(loginRequest.email.trim().lowercase())
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.WRONG_EMAIL))
            } else {
                if (user.password == hashFunction(loginRequest.password)) {
                    val token = userUseCase.generateToken(user)
                    call.respond(HttpStatusCode.OK, BaseResponse(true, token))
                } else {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.Error.WRONG_PASSWORD))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: Constants.Error.GENERAL_ERROR))
        }
    }
}
