package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object MaterialTable: Table() {
    val id: Column<Int> = integer(name = "id").autoIncrement()
    val name: Column<String> = varchar(name = "name", length = 30)
}