package data.model

import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class OrderModel(
    val id: Int = 0,
    val customer: UserModel,
    val employee: UserModel?,
    val statusId: Int,
    val totalPrice: Double,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val completedAt: LocalDateTime?,
    val comment: String?,
)