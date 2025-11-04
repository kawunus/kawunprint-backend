package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object OrderHistoryTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val orderId: Column<Int> = integer("order_id").references(OrderTable.id)
    val employeeId: Column<Int> = integer("employee_id").references(UserTable.id)
    val status: Column<String?> = varchar("status", 50).nullable()
    val comment: Column<String> = varchar("comment", 500)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}
