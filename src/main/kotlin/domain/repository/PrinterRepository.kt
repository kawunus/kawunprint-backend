package domain.repository

import data.model.PrinterModel

interface PrinterRepository {
    suspend fun getAllPrinters(): List<PrinterModel>
    suspend fun getPrinterById(id: Int): PrinterModel?
    suspend fun createPrinter(printer: PrinterModel)
    suspend fun updatePrinter(printer: PrinterModel)
    suspend fun deletePrinter(id: Int)
    suspend fun getPrintersByActiveState(isActive: Boolean): List<PrinterModel>
    suspend fun updatePrinterActiveState(id: Int, isActive: Boolean)
}
