package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OrderFileTable : Table("order_files") {
    val id: Column<Int> = integer("id").autoIncrement()
    val orderId: Column<Int> = integer("order_id").references(OrderTable.id, onDelete = ReferenceOption.CASCADE)
    val fileName: Column<String> = varchar("file_name", 255)
    val fileUrl: Column<String> = varchar("file_url", 2048)
    val firebaseStoragePath: Column<String> = varchar("firebase_storage_path", 2048)
    val fileSize: Column<Long> = long("file_size")
    val mimeType: Column<String> = varchar("mime_type", 255)
    val uploadedAt = datetime("uploaded_at")
    val uploadedBy: Column<Int> = integer("uploaded_by")

    override val primaryKey = PrimaryKey(id)
}
