package data.model.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import su.kawunprint.data.model.tables.FilamentTable
import su.kawunprint.data.model.tables.UserTable

object CartTable : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id)
    val filamentId = integer("filament_id").references(FilamentTable.id).nullable()
    val filePath = varchar("file_path", 255) // путь к загруженному STL или GCODE
    val weight = double("weight") // предполагаемый вес в граммах
    val estimatedPrice = double("estimated_price").default(0.0)
    val comment = varchar("comment", 300).nullable() // например, "хочу глянцевую поверхность"
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}