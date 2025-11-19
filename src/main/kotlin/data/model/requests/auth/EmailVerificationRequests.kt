package data.model.requests.auth

import kotlinx.serialization.Serializable

@Serializable
data class SendVerificationCodeRequest(
    val email: String
)

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String
)

