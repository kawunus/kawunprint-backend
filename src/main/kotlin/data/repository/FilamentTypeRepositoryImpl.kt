package su.kawunprint.data.repository

import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.domain.repository.FilamentTypeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.DatabaseFactory.dbQuery
import su.kawunprint.data.model.tables.FilamentTypeTable

class FilamentTypeRepositoryImpl : FilamentTypeRepository {

    override suspend fun getAllFilamentTypes(): List<FilamentTypeModel> {
        return dbQuery {
            FilamentTypeTable.selectAll().map { rowToModel(it) }
        }
    }

    override suspend fun createFilamentType(type: FilamentTypeModel) {
        dbQuery {
            FilamentTypeTable.insert { table ->
                table[name] = type.name
                table[description] = type.description
            }
        }
    }

    override suspend fun deleteFilamentType(type: FilamentTypeModel) {
        dbQuery {
            FilamentTypeTable.deleteWhere { FilamentTypeTable.id eq type.id }
        }
    }

    override suspend fun updateFilamentType(type: FilamentTypeModel) {
        dbQuery {
            FilamentTypeTable.update({ FilamentTypeTable.id eq type.id }) { table ->
                table[name] = type.name
                table[description] = type.description
            }
        }
    }

    private fun rowToModel(row: ResultRow): FilamentTypeModel {
        return FilamentTypeModel(
            id = row[FilamentTypeTable.id],
            name = row[FilamentTypeTable.name],
            description = row[FilamentTypeTable.description]
        )
    }
}
