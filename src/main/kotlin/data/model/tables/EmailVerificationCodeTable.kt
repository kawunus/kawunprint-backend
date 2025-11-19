package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object EmailVerificationCodeTable : Table("email_verification_codes") {
    val id: Column<Int> = integer("id").autoIncrement()
    val userId: Column<Int> = integer("user_id").references(UserTable.id)
    val code: Column<String> = varchar("code", 6)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

