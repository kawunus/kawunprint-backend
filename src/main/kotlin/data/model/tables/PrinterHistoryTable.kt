package data.model.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object PrinterHistoryTable : Table() {
    val id = integer("id").autoIncrement()
    val printerId = integer("printer_id").index()
    val employeeId = integer("employee_id").nullable().index()
    val occurredAt = datetime("occurred_at").defaultExpression(CurrentDateTime)
    val comment = varchar("comment", length = 512)
}