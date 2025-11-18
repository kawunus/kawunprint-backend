package data.repository

import data.model.CartModel
import data.model.UserModel
import data.model.tables.CartTable
import domain.repository.CartRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.data.model.getRoleByString
import su.kawunprint.data.model.tables.FilamentTable
import su.kawunprint.data.model.tables.FilamentTypeTable
import su.kawunprint.data.model.tables.UserTable
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

class CartRepositoryImpl : CartRepository {

    override suspend fun createCartItem(item: CartModel): CartModel? {
        val key = dbQuery {
            CartTable.insert {
                it[userId] = item.user.id
                it[filamentId] = item.filament?.id
                it[filePath] = item.filePath
                it[weight] = item.weight
                it[estimatedPrice] = item.estimatedPrice
                it[comment] = item.comment
                it[createdAt] = LocalDateTime.now()
            }.resultedValues?.firstOrNull()?.get(CartTable.id)
        }
        return key?.let { getCartItemById(it) }
    }

    override suspend fun getCartItemsByUserId(userId: Int): List<CartModel> {
        return dbQuery {
            val query = (CartTable innerJoin UserTable)
                .leftJoin(FilamentTable)
                .leftJoin(FilamentTypeTable)

            query.select { CartTable.userId eq userId }
                .map { rowToModelJoined(it) }
        }
    }

    override suspend fun getCartItemById(id: Int): CartModel? {
        return dbQuery {
            val query = (CartTable innerJoin UserTable)
                .leftJoin(FilamentTable)
                .leftJoin(FilamentTypeTable)

            query.select { CartTable.id eq id }
                .map { rowToModelJoined(it) }
                .singleOrNull()
        }
    }

    override suspend fun updateCartItem(item: CartModel) {
        dbQuery {
            CartTable.update({ CartTable.id eq item.id }) {
                it[filamentId] = item.filament?.id
                it[filePath] = item.filePath
                it[weight] = item.weight
                it[estimatedPrice] = item.estimatedPrice
                it[comment] = item.comment
            }
        }
    }

    override suspend fun deleteCartItem(id: Int) {
        dbQuery {
            CartTable.deleteWhere { CartTable.id eq id }
        }
    }

    private fun rowToModelJoined(row: ResultRow): CartModel {
        val user = UserModel(
            id = row[UserTable.id],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            email = row[UserTable.email],
            phoneNumber = row[UserTable.phoneNumber],
            password = row[UserTable.password],
            telegramAccount = row[UserTable.telegramAccount],
            role = row[UserTable.role].getRoleByString()
        )

        val filament = if (row.hasValue(FilamentTable.id) && row[FilamentTable.id] != null) {
            val filamentType = if (row.hasValue(FilamentTypeTable.id) && row[FilamentTypeTable.id] != null) {
                FilamentTypeModel(
                    id = row[FilamentTypeTable.id],
                    name = row[FilamentTypeTable.name],
                    description = row[FilamentTypeTable.description],
                )
            } else null

            filamentType?.let {
                FilamentModel(
                    id = row[FilamentTable.id],
                    color = row[FilamentTable.color],
                    type = it,
                    pricePerGram = row[FilamentTable.pricePerGram],
                    residue = row[FilamentTable.residue],
                    hexColor = row[FilamentTable.hexColor],
                )
            }
        } else null

        return CartModel(
            id = row[CartTable.id],
            user = user,
            filament = filament,
            filePath = row[CartTable.filePath],
            weight = row[CartTable.weight],
            estimatedPrice = row[CartTable.estimatedPrice],
            comment = row[CartTable.comment],
            createdAt = row[CartTable.createdAt],
        )
    }
}
