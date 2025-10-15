package su.kawunprint.domain.repository

import data.model.UserModel
import su.kawunprint.data.model.RoleModel

interface UserRepository {

    suspend fun getUserByEmail(email: String): UserModel?

    suspend fun createUser(user: UserModel)

    suspend fun updateUser(user: UserModel)

    suspend fun deleteUserById(userId: Int)

    suspend fun updateUserRoleById(userId: Int, role: RoleModel)

    suspend fun getAllUsers(): List<UserModel>

    suspend fun getUserById(userId: Int): UserModel?
}