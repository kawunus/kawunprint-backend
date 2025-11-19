package data.model

import java.time.LocalDateTime

data class EmailVerificationCodeModel(
    val id: Int = 0,
    val userId: Int,
    val code: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

