package data.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class OrderHistoryModel(
    val id: Int = 0,
    val orderId: Int,
    val employee: UserModel,
    val status: String?,
    val comment: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
) : Principal