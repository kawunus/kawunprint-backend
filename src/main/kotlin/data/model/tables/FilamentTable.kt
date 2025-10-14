package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object FilamentTable: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val color: Column<String> = varchar(name = "color", length = 30)
    val typeId: Column<Int> = integer("type_id").references(FilamentTypeTable.id)
    val pricePerGram: Column<Int> = integer("price_per_gram")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}