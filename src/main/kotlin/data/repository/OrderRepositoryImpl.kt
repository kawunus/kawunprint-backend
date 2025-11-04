package su.kawunprint.data.repository

import data.model.OrderModel
import data.model.UserModel
import domain.repository.OrderRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.getRoleByString
import su.kawunprint.data.model.tables.OrderTable
import su.kawunprint.data.model.tables.UserTable
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

class OrderRepositoryImpl : OrderRepository {

    override suspend fun createOrder(order: OrderModel): OrderModel? {
        val key = dbQuery {
            OrderTable.insert {
                it[customerId] = order.customer.id
                it[employeeId] = order.employee?.id
                it[status] = order.status
                it[totalPrice] = order.totalPrice
                it[createdAt] = LocalDateTime.now()
                it[completedAt] = order.completedAt
                it[comment] = order.comment
            }.resultedValues?.firstOrNull()?.get(OrderTable.id)
        }
        return key?.let { getOrderById(it) }
    }

    override suspend fun getAllOrders(): List<OrderModel> {
        return dbQuery {
            val customer = UserTable.alias("customer")
            val employee = UserTable.alias("employee")

            OrderTable
                .join(customer, JoinType.INNER, OrderTable.customerId, customer[UserTable.id])
                .join(employee, JoinType.LEFT, OrderTable.employeeId, employee[UserTable.id])
                .selectAll()
                .map { rowToModel(it, customer, employee) }
        }
    }

    override suspend fun getOrdersByCustomerId(customerId: Int): List<OrderModel> {
        return dbQuery {
            val customer = UserTable.alias("customer")
            val employee = UserTable.alias("employee")

            OrderTable
                .join(customer, JoinType.INNER, OrderTable.customerId, customer[UserTable.id])
                .join(employee, JoinType.LEFT, OrderTable.employeeId, employee[UserTable.id])
                .select { OrderTable.customerId eq customerId }
                .map { rowToModel(it, customer, employee) }
        }
    }

    override suspend fun getOrderById(id: Int): OrderModel? {
        return dbQuery {
            val customer = UserTable.alias("customer")
            val employee = UserTable.alias("employee")

            OrderTable
                .join(customer, JoinType.INNER, OrderTable.customerId, customer[UserTable.id])
                .join(employee, JoinType.LEFT, OrderTable.employeeId, employee[UserTable.id])
                .select { OrderTable.id eq id }
                .map { rowToModel(it, customer, employee) }
                .singleOrNull()
        }
    }

    override suspend fun updateOrder(order: OrderModel) {
        dbQuery {
            OrderTable.update({ OrderTable.id eq order.id }) {
                it[employeeId] = order.employee?.id
                it[status] = order.status
                it[totalPrice] = order.totalPrice
                it[completedAt] = order.completedAt
                it[comment] = order.comment
            }
        }
    }

    override suspend fun deleteOrder(id: Int) {
        dbQuery {
            OrderTable.deleteWhere { OrderTable.id eq id }
        }
    }

    private fun rowToModel(row: ResultRow, customer: Alias<UserTable>, employee: Alias<UserTable>): OrderModel {
        val customerModel = UserModel(
            id = row[customer[UserTable.id]],
            firstName = row[customer[UserTable.firstName]],
            lastName = row[customer[UserTable.lastName]],
            password = row[customer[UserTable.password]],
            phoneNumber = row[customer[UserTable.phoneNumber]],
            email = row[customer[UserTable.email]],
            role = row[customer[UserTable.role]].getRoleByString()
        )

        val employeeModel = if (row.hasValue(employee[UserTable.id]) && row[employee[UserTable.id]] != null) {
            UserModel(
                id = row[employee[UserTable.id]],
                firstName = row[employee[UserTable.firstName]],
                lastName = row[employee[UserTable.lastName]],
                password = row[employee[UserTable.password]],
                phoneNumber = row[employee[UserTable.phoneNumber]],
                email = row[employee[UserTable.email]],
                role = row[employee[UserTable.role]].getRoleByString()
            )
        } else null

        return OrderModel(
            id = row[OrderTable.id],
            customer = customerModel,
            employee = employeeModel,
            status = row[OrderTable.status],
            totalPrice = row[OrderTable.totalPrice],
            createdAt = row[OrderTable.createdAt],
            completedAt = row[OrderTable.completedAt],
            comment = row[OrderTable.comment]
        )
    }
}
