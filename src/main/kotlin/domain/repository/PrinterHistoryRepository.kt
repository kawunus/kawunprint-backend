package domain.repository

import data.model.PrinterHistoryModel

interface PrinterHistoryRepository {
    suspend fun getAll(): List<PrinterHistoryModel>
    suspend fun getById(id: Int): PrinterHistoryModel?
    suspend fun create(record: PrinterHistoryModel): PrinterHistoryModel?
    suspend fun update(record: PrinterHistoryModel)
    suspend fun delete(id: Int)
}
