package data.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import su.kawunprint.data.model.RoleModel

@Serializable
data class UserModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val telegramAccount: String? = null,
    val role: RoleModel,
    val isActive: Boolean = false,
) : Principal
