package data.repository

import data.model.PrinterHistoryModel
import data.model.tables.PrinterHistoryTable
import domain.repository.PrinterHistoryRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

class PrinterHistoryRepositoryImpl : PrinterHistoryRepository {

    override suspend fun getAll(): List<PrinterHistoryModel> = dbQuery {
        PrinterHistoryTable.selectAll().map { rowToModel(it) }
    }

    override suspend fun getById(id: Int): PrinterHistoryModel? = dbQuery {
        PrinterHistoryTable
            .select { PrinterHistoryTable.id eq id }
            .map { rowToModel(it) }
            .singleOrNull()
    }

    override suspend fun create(record: PrinterHistoryModel): PrinterHistoryModel? {
        val key = dbQuery {
            PrinterHistoryTable.insert {
                it[printerId] = record.printerId
                it[employeeId] = record.employeeId
                it[comment] = record.comment
                it[occurredAt] = LocalDateTime.now()
            }.resultedValues?.firstOrNull()?.get(PrinterHistoryTable.id)
        }
        return key?.let { getById(it) }
    }

    override suspend fun update(record: PrinterHistoryModel) {
        dbQuery {
            PrinterHistoryTable.update({ PrinterHistoryTable.id eq record.id }) {
                it[comment] = record.comment
                it[printerId] = record.printerId
                it[employeeId] = record.employeeId
            }
        }
    }

    override suspend fun delete(id: Int) {
        dbQuery {
            PrinterHistoryTable.deleteWhere { PrinterHistoryTable.id eq id }
        }
    }

    private fun rowToModel(row: ResultRow) = PrinterHistoryModel(
        id = row[PrinterHistoryTable.id],
        printerId = row[PrinterHistoryTable.printerId],
        employeeId = row[PrinterHistoryTable.employeeId],
        occurredAt = row[PrinterHistoryTable.occurredAt],
        comment = row[PrinterHistoryTable.comment]
    )
}
