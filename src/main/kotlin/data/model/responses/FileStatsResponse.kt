package data.model.responses

import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class FileStatsResponse(
    val orderId: Int,
    val totalFiles: Int,
    val maxFiles: Int,
    val remainingSlots: Int,
    val totalSize: Long,
    val totalSizeFormatted: String,
    val maxFileSize: Int,
    val maxFileSizeFormatted: String,
    val canUploadMore: Boolean,
    val files: List<FileInfoResponse>
)

@Serializable
data class FileInfoResponse(
    val id: Int,
    val fileName: String,
    val size: Long,
    val sizeFormatted: String,
    val mimeType: String,
    val isImage: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val uploadedAt: LocalDateTime
)

