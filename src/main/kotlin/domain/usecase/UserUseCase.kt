package su.kawunprint.domain.usecase

import data.model.UserModel
import su.kawunprint.authentification.JwtService
import su.kawunprint.domain.repository.UserRepository

class UserUseCase(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    suspend fun createUser(userModel: UserModel) = userRepository.createUser(userModel)

    suspend fun getUserByEmail(email: String) = userRepository.getUserByEmail(email)

    fun generateToken(userModel: UserModel) = jwtService.generateToken(userModel)
}