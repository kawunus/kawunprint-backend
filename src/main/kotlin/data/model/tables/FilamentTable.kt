package su.kawunprint.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object FilamentTable: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val color: Column<String> = varchar(name = "color", length = 30)
    val typeId: Column<Int> = integer("type_id").references(FilamentTypeTable.id)
    val pricePerGram: Column<Double> = double("price_per_gram")
    val residue: Column<Int> = integer("residue")
    val hexColor: Column<String> = varchar(name = "hex_color", length = 50)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}