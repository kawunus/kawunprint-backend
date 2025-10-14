package data.model

import io.ktor.server.auth.*
import su.kawunprint.data.model.RoleModel

data class UserModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val role: RoleModel,
    val isActive: Boolean = false,
) : Principal
