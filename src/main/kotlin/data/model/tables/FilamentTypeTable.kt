package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object FilamentTypeTable: Table() {
    val id: Column<Int> = integer(name = "id").autoIncrement()
    val name: Column<String> = varchar(name = "name", length = 10).uniqueIndex()
    val description: Column<String> = varchar(name = "description", length = 300)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}