package data.model

import kotlinx.serialization.Serializable
import utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class PrinterHistoryModel(
    val id: Int = 0,
    val printerId: Int,
    val employeeId: Int?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val occurredAt: LocalDateTime = LocalDateTime.now(),
    val comment: String
)
