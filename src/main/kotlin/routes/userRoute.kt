package su.kawunprint.routes

import data.model.UserModel
import data.model.requests.user.UpdateSelfUserRequest
import data.model.requests.user.UpdateUserRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.authentification.hashPassword
import su.kawunprint.data.model.RoleModel
import su.kawunprint.data.model.requests.auth.LoginRequest
import su.kawunprint.data.model.requests.auth.RegisterRequest
import su.kawunprint.data.model.responses.BaseResponse
import su.kawunprint.domain.usecase.UserUseCase
import su.kawunprint.utils.Constants

fun Route.userRoute() {
    val userUseCase: UserUseCase by inject<UserUseCase>()
    val hashFunction = { p: String -> hashPassword(password = p) }

    post("/api/v1/register") {
        val registerRequest = call.receive<RegisterRequest>()

        try {
            val user = UserModel(
                id = 0,
                email = registerRequest.email.trim().lowercase(),
                password = hashFunction(registerRequest.password),
                firstName = registerRequest.firstName.trim(),
                lastName = registerRequest.lastName.trim(),
                phoneNumber = registerRequest.phoneNumber.trim(),
                telegramAccount = null,
                role = RoleModel.CLIENT,
                isActive = true
            )

            userUseCase.createUser(user)
            val token = userUseCase.generateToken(user)
            call.respond(HttpStatusCode.OK, BaseResponse(true, token))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: Constants.ErrorMessages.GENERAL))
        }
    }

    post("/api/v1/login") {
        val loginRequest = call.receiveNullable<LoginRequest>() ?: run {
            return@post call.respond(HttpStatusCode.BadRequest, BaseResponse(false, Constants.ErrorMessages.GENERAL))
        }

        try {
            val user = userUseCase.getUserByEmail(loginRequest.email.trim().lowercase())
            if (user == null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        false, Constants.ErrorMessages
                            .WRONG_EMAIL
                    )
                )
            } else {
                if (user.password == hashFunction(loginRequest.password)) {
                    val token = userUseCase.generateToken(user)
                    return@post call.respond(HttpStatusCode.OK, BaseResponse(true, token))
                } else {
                    return@post call.respond(
                        HttpStatusCode.BadRequest, BaseResponse(
                            false, Constants.ErrorMessages
                                .WRONG_PASSWORD
                        )
                    )
                }
            }
        } catch (e: Exception) {
            return@post call.respond(
                HttpStatusCode.Conflict, BaseResponse(
                    false, e.message ?: Constants
                        .ErrorMessages.GENERAL
                )
            )
        }
    }

    authenticate("jwt") {
        route("/api/v1/users") {
            // Endpoints for the authenticated user to manage their own profile
            get("/me") {
                val principal = call.principal<UserModel>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val user = userUseCase.getUserById(principal.id) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, user)
            }

            put("/me") {
                val principal = call.principal<UserModel>() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<UpdateSelfUserRequest>()

                try {
                    val updated = userUseCase.updateUser(
                        UserModel(
                            id = principal.id,
                            firstName = request.firstName,
                            lastName = request.lastName,
                            email = request.email,
                            phoneNumber = request.phoneNumber,
                            telegramAccount = principal.telegramAccount,
                            password = hashFunction(request.password),
                            role = principal.role,
                            isActive = principal.isActive
                        )
                    )
                    call.respond(HttpStatusCode.OK, updated)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            delete("/me") {
                val principal = call.principal<UserModel>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                try {
                    userUseCase.deleteUserById(principal.id)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            get {
                call.authenticateWithRole(RoleModel.ADMIN)
                try {
                    val users = userUseCase.getAllUsers()
                    call.respond(HttpStatusCode.OK, users)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            get("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val user = userUseCase.getUserById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, user)
            }

            delete("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                try {
                    userUseCase.deleteUserById(id)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post("/{id}") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateUserRequest>()

                try {
                    val updatedUser = userUseCase.updateUser(
                        UserModel(
                            id = id,
                            firstName = request.firstName,
                            lastName = request.lastName,
                            email = request.email,
                            phoneNumber = request.phoneNumber,
                            telegramAccount = null,
                            password = hashFunction(request.password),
                            role = request.role,
                            isActive = request.isActive
                        )
                    )
                    call.respond(HttpStatusCode.OK, updatedUser)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post("/{id}/role") {
                call.authenticateWithRole(RoleModel.ADMIN)
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val newRole = call.parameters["role"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                try {
                    userUseCase.updateUserRoleById(id, RoleModel.valueOf(newRole.uppercase()))
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
