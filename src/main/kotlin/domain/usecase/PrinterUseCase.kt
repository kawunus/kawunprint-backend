package domain.usecase

import data.model.PrinterModel
import domain.repository.PrinterRepository

class PrinterUseCase(private val printerRepository: PrinterRepository) {

    suspend fun getAllPrinters() = printerRepository.getAllPrinters()
    suspend fun getPrinterById(id: Int) = printerRepository.getPrinterById(id)
    suspend fun createPrinter(printer: PrinterModel) = printerRepository.createPrinter(printer)
    suspend fun updatePrinter(printer: PrinterModel) = printerRepository.updatePrinter(printer)
    suspend fun deletePrinter(id: Int) = printerRepository.deletePrinter(id)
    suspend fun getPrintersByActiveState(isActive: Boolean) =
        printerRepository.getPrintersByActiveState(isActive)

    suspend fun updatePrinterActiveState(id: Int, isActive: Boolean) =
        printerRepository.updatePrinterActiveState(id, isActive)
}
