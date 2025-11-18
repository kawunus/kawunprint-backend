package data.repository

import data.model.OrderHistoryModel
import data.model.UserModel
import domain.repository.OrderHistoryRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import su.kawunprint.data.model.getRoleByString
import su.kawunprint.data.model.tables.OrderHistoryTable
import su.kawunprint.data.model.tables.UserTable
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

class OrderHistoryRepositoryImpl : OrderHistoryRepository {
    override suspend fun addHistoryEntry(entry: OrderHistoryModel): OrderHistoryModel? {
        val key = dbQuery {
            OrderHistoryTable.insert {
                it[orderId] = entry.orderId
                it[employeeId] = entry.employee.id
                it[statusId] = entry.statusId
                it[comment] = entry.comment
                it[createdAt] = LocalDateTime.now()
            }.resultedValues?.firstOrNull()?.get(OrderHistoryTable.id)
        }
        return key?.let { getHistoryEntryById(it) }
    }

    override suspend fun getHistoryForOrder(orderId: Int): List<OrderHistoryModel> {
        return dbQuery {
            (OrderHistoryTable innerJoin UserTable)
                .select { OrderHistoryTable.orderId eq orderId }
                .orderBy(OrderHistoryTable.createdAt, SortOrder.DESC)
                .map { rowToModel(it) }
        }
    }

    private suspend fun getHistoryEntryById(id: Int): OrderHistoryModel? {
        return dbQuery {
            (OrderHistoryTable innerJoin UserTable)
                .select { OrderHistoryTable.id eq id }
                .map { rowToModel(it) }
                .singleOrNull()
        }
    }

    private fun rowToModel(row: ResultRow): OrderHistoryModel {
        val employee = UserModel(
            id = row[UserTable.id],
            email = row[UserTable.email],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            password = row[UserTable.password],
            phoneNumber = row[UserTable.phoneNumber],
            telegramAccount = row[UserTable.telegramAccount],
            role = row[UserTable.role].getRoleByString()
        )

        return OrderHistoryModel(
            id = row[OrderHistoryTable.id],
            orderId = row[OrderHistoryTable.orderId],
            employee = employee,
            statusId = row[OrderHistoryTable.statusId],
            comment = row[OrderHistoryTable.comment],
            createdAt = row[OrderHistoryTable.createdAt]
        )
    }
}