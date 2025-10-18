package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PrinterTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val isMulticolor: Column<Boolean> = bool("is_multicolor").default(false)
    val isActive: Column<Boolean> = bool("is_active").default(true)
    val description: Column<String?> = varchar("description", 200).nullable()

    override val primaryKey = PrimaryKey(id)
}
