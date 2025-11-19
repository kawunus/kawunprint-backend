package data.model

import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class OrderFileModel(
    val id: Int = 0,
    val orderId: Int,
    val fileName: String,
    val fileUrl: String,
    val firebaseStoragePath: String,
    val fileSize: Long,
    val mimeType: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val uploadedAt: LocalDateTime,
    val uploadedBy: Int
)

