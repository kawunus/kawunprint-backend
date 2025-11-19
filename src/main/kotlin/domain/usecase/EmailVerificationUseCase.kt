package domain.usecase

import domain.repository.EmailVerificationRepository
import su.kawunprint.authentification.hashPassword
import su.kawunprint.domain.repository.UserRepository
import su.kawunprint.services.EmailService
import java.time.LocalDateTime
import kotlin.random.Random

class EmailVerificationUseCase(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {

    companion object {
        const val CODE_EXPIRATION_MINUTES = 15L
    }

    suspend fun sendVerificationCode(email: String): Boolean {
        // Проверяем существование пользователя
        val user = userRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("Email not found")

        // Генерируем 6-значный код
        val code = generateCode()

        // Удаляем старые коды для этого пользователя
        emailVerificationRepository.deleteCodesForUser(user.id)

        // Создаем новый код с временем истечения 15 минут
        val expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES)
        emailVerificationRepository.createCode(user.id, code, expiresAt)

        // Отправляем код на email
        try {
            emailService.sendVerificationCode(email, code)
            return true
        } catch (e: Exception) {
            println("❌ Failed to send verification code: ${e.message}")
            return false
        }
    }

    suspend fun verifyEmail(email: String, code: String): Boolean {
        // Проверяем существование пользователя
        val user = userRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("Email not found")

        // Проверяем код
        emailVerificationRepository.getValidCode(user.id, code)
            ?: return false

        // Активируем пользователя
        userRepository.activateUser(user.id)

        // Удаляем все коды для этого пользователя
        emailVerificationRepository.deleteCodesForUser(user.id)

        return true
    }

    suspend fun cleanupExpiredCodes(): Int {
        return emailVerificationRepository.deleteExpiredCodes()
    }

    suspend fun resetPassword(email: String): Boolean {
        // Проверяем существование пользователя
        val user = userRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("Email not found")

        // Генерируем новый случайный пароль (8-12 символов)
        val newPassword = generateRandomPassword()

        // Хешируем новый пароль
        val hashedPassword = hashPassword(newPassword)

        // Обновляем пароль пользователя в БД
        val updatedUser = user.copy(password = hashedPassword)
        userRepository.updateUser(updatedUser)

        // Отправляем новый пароль на email
        try {
            emailService.sendPasswordResetEmail(email, newPassword)
            return true
        } catch (e: Exception) {
            println("❌ Failed to send password reset email: ${e.message}")
            return false
        }
    }

    private fun generateCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%"
        val length = Random.nextInt(8, 13) // от 8 до 12 символов
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
