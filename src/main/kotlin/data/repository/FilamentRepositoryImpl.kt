package su.kawunprint.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.data.model.tables.FilamentTable
import su.kawunprint.data.model.tables.FilamentTypeTable
import su.kawunprint.domain.repository.FilamentRepository
import su.kawunprint.plugins.Databases.dbQuery

class FilamentRepositoryImpl : FilamentRepository {

    override suspend fun createFilament(filament: FilamentModel) {
        dbQuery {
            FilamentTable.insert { filamentTable ->
                filamentTable[color] = filament.color
                filamentTable[typeId] = filament.type.id
                filamentTable[pricePerGram] = filament.pricePerGram
                filamentTable[residue] = filament.residue
                filamentTable[hexColor] = filament.hexColor
            }
        }
    }

    override suspend fun getAllFilaments(): List<FilamentModel> {
        return dbQuery {
            (FilamentTable innerJoin FilamentTypeTable)
                .selectAll()
                .map { rowToModelJoined(it) }
        }
    }

    override suspend fun getFilamentsByType(type: FilamentTypeModel): List<FilamentModel> {
        return dbQuery {
            (FilamentTable innerJoin FilamentTypeTable)
                .select { FilamentTable.typeId eq type.id }
                .map { rowToModelJoined(it) }
        }
    }

    override suspend fun updateFilament(filament: FilamentModel) {
        dbQuery {
            FilamentTable.update({ FilamentTable.id eq filament.id }) { filamentTable ->
                filamentTable[color] = filament.color
                filamentTable[typeId] = filament.type.id
                filamentTable[pricePerGram] = filament.pricePerGram
                filamentTable[residue] = filament.residue
                filamentTable[hexColor] = filament.hexColor
            }
        }
    }

    override suspend fun deleteFilament(filament: FilamentModel) {
        dbQuery {
            FilamentTable.deleteWhere { FilamentTable.id eq filament.id }
        }
    }

    private fun rowToModelJoined(row: ResultRow): FilamentModel {
        val type = FilamentTypeModel(
            id = row[FilamentTypeTable.id],
            name = row[FilamentTypeTable.name],
            description = row[FilamentTypeTable.description],
        )

        return FilamentModel(
            id = row[FilamentTable.id],
            color = row[FilamentTable.color],
            type = type,
            pricePerGram = row[FilamentTable.pricePerGram],
            residue = row[FilamentTable.residue],
            hexColor = row[FilamentTable.hexColor],
        )
    }

    override suspend fun getFilamentById(id: Int): FilamentModel? {
        return dbQuery {
            (FilamentTable innerJoin FilamentTypeTable)
                .select { FilamentTable.id eq id }
                .map { rowToModelJoined(it) }
                .singleOrNull()
        }
    }
}
