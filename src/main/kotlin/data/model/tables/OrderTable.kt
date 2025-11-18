package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OrderTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val customerId: Column<Int> = integer("customer_id").references(UserTable.id)
    val employeeId: Column<Int?> = integer("employee_id").references(UserTable.id).nullable()
    val statusId: Column<Int> = integer("status_id").references(OrderStatusTable.id) // связь с OrderStatusTable
    val totalPrice: Column<Double> = double("total_price").default(0.0)
    val createdAt = datetime("created_at")
    val completedAt = datetime("completed_at").nullable()
    val comment: Column<String?> = varchar("comment", 300).nullable()

    override val primaryKey = PrimaryKey(id)
}