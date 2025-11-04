package data.model

import kotlinx.serialization.Serializable
import su.kawunprint.data.model.FilamentModel
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class CartModel(
    val id: Int = 0,
    val user: UserModel,
    val filament: FilamentModel?,
    val filePath: String,
    val weight: Double,
    val estimatedPrice: Double,
    val comment: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
)
