package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OrderFileTable : Table("order_files") {
    val id: Column<Int> = integer("id").autoIncrement()
    val orderId: Column<Int> = integer("order_id").references(OrderTable.id)
    val fileName: Column<String> = varchar("file_name", 255)
    val fileUrl: Column<String> = varchar("file_url", 512)
    val firebaseStoragePath: Column<String> = varchar("firebase_storage_path", 512)
    val fileSize: Column<Long> = long("file_size") // bytes
    val mimeType: Column<String> = varchar("mime_type", 100)
    val uploadedAt = datetime("uploaded_at")
    val uploadedBy: Column<Int> = integer("uploaded_by").references(UserTable.id)

    override val primaryKey = PrimaryKey(id)
}

