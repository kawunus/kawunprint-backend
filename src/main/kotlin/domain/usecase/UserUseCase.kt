package su.kawunprint.domain.usecase

import data.model.UserModel
import su.kawunprint.authentification.JwtService
import su.kawunprint.data.model.RoleModel
import su.kawunprint.domain.repository.UserRepository

class UserUseCase(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    suspend fun createUser(userModel: UserModel) = userRepository.createUser(userModel)

    suspend fun getUserByEmail(email: String) = userRepository.getUserByEmail(email)

    fun generateToken(userModel: UserModel) = jwtService.generateToken(userModel)

    fun getVerifier() = jwtService.getVerifier()

    suspend fun updateUser(user: UserModel) = userRepository.updateUser(user)

    suspend fun deleteUserById(userId: Int) = userRepository.deleteUserById(userId)

    suspend fun updateUserRoleById(userId: Int, role: RoleModel) = userRepository.updateUserRoleById(userId, role)

    suspend fun getAllUsers(): List<UserModel> = userRepository.getAllUsers()

    suspend fun getUserById(userId: Int): UserModel? = userRepository.getUserById(userId)
}