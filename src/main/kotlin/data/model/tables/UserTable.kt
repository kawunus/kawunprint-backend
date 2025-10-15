package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val email: Column<String> = varchar("email", 64).uniqueIndex()
    val password: Column<String> = varchar("password", 64)
    val firstName: Column<String> = varchar("first_name", 30)
    val lastName: Column<String> = varchar("last_name", 30)
    val role: Column<String> = varchar("user_role", 32)
    val isActive: Column<Boolean> = bool("is_active")
    val phoneNumber: Column<String> = varchar("phone", 64)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}