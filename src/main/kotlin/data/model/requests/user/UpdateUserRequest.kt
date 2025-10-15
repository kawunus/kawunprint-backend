package data.model.requests.user

import kotlinx.serialization.Serializable
import su.kawunprint.data.model.RoleModel

@Serializable
data class UpdateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val role: RoleModel,
    val isActive: Boolean
)
