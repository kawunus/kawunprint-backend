package data.model.responses.auth

import kotlinx.serialization.Serializable

@Serializable
data class SendVerificationCodeResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class VerifyEmailResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null
)

@Serializable
data class ResetPasswordResponse(
    val success: Boolean,
    val message: String
)
