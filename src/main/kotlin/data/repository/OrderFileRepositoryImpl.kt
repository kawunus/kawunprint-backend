package data.repository

import data.model.OrderFileModel
import domain.repository.OrderFileRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.tables.OrderFileTable
import su.kawunprint.plugins.Databases.dbQuery

class OrderFileRepositoryImpl : OrderFileRepository {

    override suspend fun saveFileMetadata(file: OrderFileModel): OrderFileModel? {
        val key = dbQuery {
            OrderFileTable.insert {
                it[orderId] = file.orderId
                it[fileName] = file.fileName
                it[fileUrl] = file.fileUrl
                it[firebaseStoragePath] = file.firebaseStoragePath
                it[fileSize] = file.fileSize
                it[mimeType] = file.mimeType
                it[uploadedAt] = file.uploadedAt
                it[uploadedBy] = file.uploadedBy
            }.resultedValues?.firstOrNull()?.get(OrderFileTable.id)
        }
        return key?.let { getFileById(it) }
    }

    override suspend fun getFilesByOrderId(orderId: Int): List<OrderFileModel> {
        return dbQuery {
            OrderFileTable.select { OrderFileTable.orderId eq orderId }
                .orderBy(OrderFileTable.uploadedAt, SortOrder.DESC)
                .map { rowToModel(it) }
        }
    }

    override suspend fun getFileById(fileId: Int): OrderFileModel? {
        return dbQuery {
            OrderFileTable.select { OrderFileTable.id eq fileId }
                .map { rowToModel(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteFile(fileId: Int): Boolean {
        return dbQuery {
            OrderFileTable.deleteWhere { OrderFileTable.id eq fileId } > 0
        }
    }

    override suspend fun deleteFilesByOrderId(orderId: Int): Boolean {
        return dbQuery {
            OrderFileTable.deleteWhere { OrderFileTable.orderId eq orderId } > 0
        }
    }

    override suspend fun countFilesByOrderId(orderId: Int): Int {
        return dbQuery {
            OrderFileTable.select { OrderFileTable.orderId eq orderId }
                .count()
                .toInt()
        }
    }

    private fun rowToModel(row: ResultRow): OrderFileModel {
        return OrderFileModel(
            id = row[OrderFileTable.id],
            orderId = row[OrderFileTable.orderId],
            fileName = row[OrderFileTable.fileName],
            fileUrl = row[OrderFileTable.fileUrl],
            firebaseStoragePath = row[OrderFileTable.firebaseStoragePath],
            fileSize = row[OrderFileTable.fileSize],
            mimeType = row[OrderFileTable.mimeType],
            uploadedAt = row[OrderFileTable.uploadedAt],
            uploadedBy = row[OrderFileTable.uploadedBy]
        )
    }
}
