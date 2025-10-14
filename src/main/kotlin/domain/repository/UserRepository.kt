package su.kawunprint.domain.repository

import data.model.UserModel

interface UserRepository {

    suspend fun getUserByEmail(email: String): UserModel?

    suspend fun createUser(user: UserModel)
}