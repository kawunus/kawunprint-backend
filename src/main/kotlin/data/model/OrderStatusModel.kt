package data.model

import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class OrderStatusModel(
    val id: Int = 0,
    val description: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
