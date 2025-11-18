package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object OrderStatusTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val description: Column<String> = varchar("description", length = 255)

    override val primaryKey = PrimaryKey(id)
}