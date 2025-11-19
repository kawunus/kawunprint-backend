package domain.repository

import data.model.OrderFileModel

interface OrderFileRepository {
    suspend fun saveFileMetadata(file: OrderFileModel): OrderFileModel?
    suspend fun getFilesByOrderId(orderId: Int): List<OrderFileModel>
    suspend fun getFileById(fileId: Int): OrderFileModel?
    suspend fun deleteFile(fileId: Int): Boolean
    suspend fun countFilesByOrderId(orderId: Int): Int
    suspend fun deleteFilesByOrderId(orderId: Int): Boolean
}
