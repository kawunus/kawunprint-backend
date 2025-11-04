package data.repository

import data.model.PrinterModel
import domain.repository.PrinterRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.tables.PrinterTable
import su.kawunprint.plugins.Databases.dbQuery

class PrinterRepositoryImpl : PrinterRepository {

    override suspend fun getAllPrinters(): List<PrinterModel> = dbQuery {
        PrinterTable.selectAll().map(::rowToPrinter)
    }

    override suspend fun getPrinterById(id: Int): PrinterModel? = dbQuery {
        PrinterTable.select { PrinterTable.id eq id }
            .map(::rowToPrinter)
            .singleOrNull()
    }

    override suspend fun createPrinter(printer: PrinterModel) {
        dbQuery {
            PrinterTable.insert {
                it[name] = printer.name
                it[isMulticolor] = printer.isMulticolor
                it[isActive] = printer.isActive
                it[description] = printer.description
            }
        }
    }

    override suspend fun updatePrinter(printer: PrinterModel) {
        dbQuery {
            PrinterTable.update({ PrinterTable.id eq printer.id }) {
                it[name] = printer.name
                it[isMulticolor] = printer.isMulticolor
                it[isActive] = printer.isActive
                it[description] = printer.description
            }
        }
    }

    override suspend fun deletePrinter(id: Int) {
        dbQuery {
            PrinterTable.deleteWhere { PrinterTable.id eq id }
        }
    }

    override suspend fun getPrintersByActiveState(isActive: Boolean): List<PrinterModel> = dbQuery {
        PrinterTable.select { PrinterTable.isActive eq isActive }
            .map(::rowToPrinter)
    }

    override suspend fun updatePrinterActiveState(id: Int, isActive: Boolean) {
        dbQuery {
            PrinterTable.update({ PrinterTable.id eq id }) {
                it[PrinterTable.isActive] = isActive
            }
        }
    }

    private fun rowToPrinter(row: ResultRow): PrinterModel = PrinterModel(
        id = row[PrinterTable.id],
        name = row[PrinterTable.name],
        isMulticolor = row[PrinterTable.isMulticolor],
        isActive = row[PrinterTable.isActive],
        description = row[PrinterTable.description]
    )
}
