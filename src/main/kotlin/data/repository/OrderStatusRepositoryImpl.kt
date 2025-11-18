package data.repository

import data.model.OrderStatusModel
import domain.repository.OrderStatusRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.tables.OrderStatusTable
import su.kawunprint.plugins.Databases.dbQuery

class OrderStatusRepositoryImpl : OrderStatusRepository {

    override suspend fun getAll(): List<OrderStatusModel> {
        return dbQuery {
            OrderStatusTable
                .selectAll()
                .map { rowToModel(it) }
        }
    }

    override suspend fun getById(id: Int): OrderStatusModel? {
        return dbQuery {
            OrderStatusTable
                .select { OrderStatusTable.id eq id }
                .map { rowToModel(it) }
                .singleOrNull()
        }
    }

    override suspend fun create(status: OrderStatusModel): OrderStatusModel? {
        val key = dbQuery {
            OrderStatusTable.insert {
                it[description] = status.description
            }.resultedValues?.firstOrNull()?.get(OrderStatusTable.id)
        }
        return key?.let { getById(it) }
    }

    override suspend fun update(status: OrderStatusModel) {
        dbQuery {
            OrderStatusTable.update({ OrderStatusTable.id eq status.id }) {
                it[description] = status.description
            }
        }
    }

    override suspend fun delete(id: Int) {
        dbQuery {
            OrderStatusTable.deleteWhere { OrderStatusTable.id eq id }
        }
    }

    private fun rowToModel(row: ResultRow) = OrderStatusModel(
        id = row[OrderStatusTable.id],
        description = row[OrderStatusTable.description],
    )
}
