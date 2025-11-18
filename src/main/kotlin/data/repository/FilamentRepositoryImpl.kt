package su.kawunprint.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.data.model.tables.FilamentTable
import su.kawunprint.data.model.tables.FilamentTypeTable
import su.kawunprint.domain.repository.FilamentRepository
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

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

    override suspend fun consumeFilamentForOrder(
        orderId: Int,
        filamentId: Int,
        gramsUsed: Int,
        employeeId: Int?,
        comment: String?
    ): Pair<Int, Int> {
        return dbQuery {
            // load filament and type
            val filamentRow = (FilamentTable innerJoin FilamentTypeTable)
                .select { FilamentTable.id eq filamentId }
                .singleOrNull() ?: throw NoSuchElementException("filament_not_found")

            val filamentResidue = filamentRow[FilamentTable.residue]
            if (filamentResidue < gramsUsed) {
                throw IllegalStateException("insufficient:${filamentResidue}")
            }

            val pricePerGram = filamentRow[FilamentTable.pricePerGram]
            val cost = gramsUsed * pricePerGram

            // ensure order exists
            val orderRow =
                su.kawunprint.data.model.tables.OrderTable.select { su.kawunprint.data.model.tables.OrderTable.id eq orderId }
                    .singleOrNull()
                    ?: throw NoSuchElementException("order_not_found")

            val currentTotal = orderRow[su.kawunprint.data.model.tables.OrderTable.totalPrice]
            val newTotal = currentTotal + cost

            // update filament residue
            val newResidue = filamentResidue - gramsUsed
            FilamentTable.update({ FilamentTable.id eq filamentId }) {
                it[FilamentTable.residue] = newResidue
            }

            // update order
            su.kawunprint.data.model.tables.OrderTable.update({ su.kawunprint.data.model.tables.OrderTable.id eq orderId }) {
                it[su.kawunprint.data.model.tables.OrderTable.totalPrice] = newTotal
                it[su.kawunprint.data.model.tables.OrderTable.statusId] = 12
            }

            // insert history
            val filamentName = filamentRow[FilamentTypeTable.name]
            val filamentColor = filamentRow[FilamentTable.color]
            val historyComment =
                "Использовано ${gramsUsed}г филамента $filamentName ($filamentColor). Стоимость: $cost BYN. ${comment ?: ""}"

            su.kawunprint.data.model.tables.OrderHistoryTable.insert {
                it[su.kawunprint.data.model.tables.OrderHistoryTable.orderId] = orderId
                it[su.kawunprint.data.model.tables.OrderHistoryTable.employeeId] = employeeId ?: 0
                it[su.kawunprint.data.model.tables.OrderHistoryTable.statusId] = 12
                it[su.kawunprint.data.model.tables.OrderHistoryTable.comment] = historyComment
                it[su.kawunprint.data.model.tables.OrderHistoryTable.createdAt] = LocalDateTime.now()
            }

            Pair(orderId, filamentId)
        }
    }
}
