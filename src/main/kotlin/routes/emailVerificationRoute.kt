package routes

import data.model.requests.auth.ResetPasswordRequest
import data.model.requests.auth.SendVerificationCodeRequest
import data.model.requests.auth.VerifyEmailRequest
import data.model.responses.auth.ResetPasswordResponse
import data.model.responses.auth.SendVerificationCodeResponse
import data.model.responses.auth.VerifyEmailResponse
import domain.usecase.EmailVerificationUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.JwtService
import su.kawunprint.domain.repository.UserRepository

fun Route.emailVerificationRoute() {
    val emailVerificationUseCase: EmailVerificationUseCase by inject()
    val userRepository: UserRepository by inject()
    val jwtService: JwtService by inject()

    route("/api/v1") {

        // Отправка кода верификации
        post("/send-verification-code") {
            try {
                val request = call.receive<SendVerificationCodeRequest>()

                val success = emailVerificationUseCase.sendVerificationCode(request.email)

                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        SendVerificationCodeResponse(
                            success = true,
                            message = "Verification code sent"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        SendVerificationCodeResponse(
                            success = false,
                            message = "Failed to send verification code"
                        )
                    )
                }
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SendVerificationCodeResponse(
                        success = false,
                        message = e.message ?: "Email not found"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    SendVerificationCodeResponse(
                        success = false,
                        message = "Internal server error"
                    )
                )
            }
        }

        // Верификация email с кодом
        post("/verify-email") {
            try {
                val request = call.receive<VerifyEmailRequest>()

                val isValid = emailVerificationUseCase.verifyEmail(request.email, request.code)

                if (isValid) {
                    // Получаем обновленного пользователя
                    val user = userRepository.getUserByEmail(request.email)
                        ?: return@post call.respond(
                            HttpStatusCode.InternalServerError,
                            VerifyEmailResponse(
                                success = false,
                                message = "User not found after verification"
                            )
                        )

                    // Генерируем новый JWT токен с isActive = true
                    val token = jwtService.generateToken(user)

                    call.respond(
                        HttpStatusCode.OK,
                        VerifyEmailResponse(
                            success = true,
                            token = token
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        VerifyEmailResponse(
                            success = false,
                            message = "Invalid or expired code"
                        )
                    )
                }
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    VerifyEmailResponse(
                        success = false,
                        message = e.message ?: "Email not found"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    VerifyEmailResponse(
                        success = false,
                        message = "Internal server error"
                    )
                )
            }
        }

        // Сброс пароля
        post("/reset-password") {
            try {
                val request = call.receive<ResetPasswordRequest>()

                val success = emailVerificationUseCase.resetPassword(request.email)

                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ResetPasswordResponse(
                            success = true,
                            message = "New password sent to email"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ResetPasswordResponse(
                            success = false,
                            message = "Failed to send password reset email"
                        )
                    )
                }
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ResetPasswordResponse(
                        success = false,
                        message = e.message ?: "Email not found"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ResetPasswordResponse(
                        success = false,
                        message = "Internal server error"
                    )
                )
            }
        }
    }
}
