package domain.repository

import data.model.EmailVerificationCodeModel
import java.time.LocalDateTime

interface EmailVerificationRepository {
    suspend fun createCode(userId: Int, code: String, expiresAt: LocalDateTime): EmailVerificationCodeModel?
    suspend fun getValidCode(userId: Int, code: String): EmailVerificationCodeModel?
    suspend fun deleteCodesForUser(userId: Int): Boolean
    suspend fun deleteExpiredCodes(): Int
}

