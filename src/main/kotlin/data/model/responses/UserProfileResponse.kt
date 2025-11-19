package data.model.responses

import kotlinx.serialization.Serializable
import su.kawunprint.data.model.RoleModel

@Serializable
data class UserProfileResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val telegramAccount: String?,
    val role: RoleModel,
    val isActive: Boolean
)

