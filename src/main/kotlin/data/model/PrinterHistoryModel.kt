package data.model

data class PrinterHistoryModel(
    val id: Int,
    val printerId: Int,
    val employeeId: Int,
    val comment: String,
    val occurredAt: Long,
)