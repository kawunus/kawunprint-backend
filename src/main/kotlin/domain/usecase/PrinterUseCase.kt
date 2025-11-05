package domain.usecase

import data.model.PrinterHistoryModel
import data.model.PrinterModel
import domain.repository.PrinterHistoryRepository
import domain.repository.PrinterRepository
import java.time.LocalDateTime

class PrinterUseCase(
    private val printerRepository: PrinterRepository,
    private val printerHistoryRepository: PrinterHistoryRepository
) {

    suspend fun getAllPrinters() = printerRepository.getAllPrinters()
    suspend fun getPrinterById(id: Int) = printerRepository.getPrinterById(id)

    suspend fun createPrinter(printer: PrinterModel, employeeId: Int?) {
        printerRepository.createPrinter(printer)

        printerHistoryRepository.create(
            PrinterHistoryModel(
                id = 0,
                printerId = printer.id,
                employeeId = employeeId,
                comment = "Принтер «${printer.name}» был добавлен в систему",
                occurredAt = LocalDateTime.now()
            )
        )
    }

    suspend fun updatePrinter(printer: PrinterModel, employeeId: Int?) {
        printerRepository.updatePrinter(printer)

        printerHistoryRepository.create(
            PrinterHistoryModel(
                id = 0,
                printerId = printer.id,
                employeeId = employeeId,
                comment = "Информация о принтере «${printer.name}» была обновлена",
                occurredAt = LocalDateTime.now()
            )
        )
    }

    suspend fun deletePrinter(id: Int) = printerRepository.deletePrinter(id)

    suspend fun getPrintersByActiveState(isActive: Boolean) =
        printerRepository.getPrintersByActiveState(isActive)

    suspend fun updatePrinterActiveState(id: Int, isActive: Boolean, employeeId: Int?) {
        printerRepository.updatePrinterActiveState(id, isActive)

        val printer = printerRepository.getPrinterById(id)
        val stateText = if (isActive) "включён" else "выключен"

        printerHistoryRepository.create(
            PrinterHistoryModel(
                id = 0,
                printerId = id,
                employeeId = employeeId,
                comment = "Принтер «${printer?.name ?: "Неизвестный"}» был $stateText",
                occurredAt = LocalDateTime.now()
            )
        )
    }
}
