package data.model.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import su.kawunprint.data.model.tables.OrderTable

object PrinterHistoryTable : Table() {
    val id = integer("id").autoIncrement()
    val printerId = integer("printer_id").index()
    val employeeId = integer("employee_id").nullable().index()
    val occurredAt = datetime("occurred_at").defaultExpression(CurrentDateTime)
    val comment = varchar("comment", length = 512)

    override val primaryKey = PrimaryKey(OrderTable.id)
}