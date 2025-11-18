package data.repository

import data.model.UserModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import su.kawunprint.data.model.RoleModel
import su.kawunprint.data.model.getRoleByString
import su.kawunprint.data.model.getStringByRole
import su.kawunprint.data.model.tables.UserTable
import su.kawunprint.domain.repository.UserRepository
import su.kawunprint.plugins.Databases.dbQuery

class UserRepositoryImpl : UserRepository {

    override suspend fun getUserByEmail(email: String): UserModel? {
       return dbQuery {
            UserTable.select {
                UserTable.email.eq(email)
            }.map {
                rowToUser(it)
            }.singleOrNull()
        }
    }

    override suspend fun createUser(user: UserModel) {
        return dbQuery {
            UserTable.insert { table ->
                table[UserTable.email] = user.email
                table[UserTable.password] = user.password
                table[UserTable.firstName] = user.firstName
                table[UserTable.lastName] = user.lastName
                table[UserTable.phoneNumber] = user.phoneNumber
                table[UserTable.telegramAccount] = user.telegramAccount
                table[UserTable.role] = user.role.getStringByRole()
                table[UserTable.isActive] = user.isActive
            }
        }
    }

    override suspend fun updateUser(user: UserModel): Unit = dbQuery {
        UserTable.update({ UserTable.id eq user.id }) {
            it[email] = user.email
            it[password] = user.password
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[phoneNumber] = user.phoneNumber
            it[UserTable.telegramAccount] = user.telegramAccount
            it[role] = user.role.getStringByRole()
            it[isActive] = user.isActive
        }
    }

    override suspend fun deleteUserById(userId: Int) {
        dbQuery {
            UserTable.deleteWhere {
                UserTable.id.eq(userId)
            }
        }
    }

    override suspend fun updateUserRoleById(userId: Int, role: RoleModel): Unit = dbQuery {
        UserTable.update({ UserTable.id eq userId }) {
            it[UserTable.role] = role.getStringByRole()
        }
    }

    override suspend fun getAllUsers(): List<UserModel> {
        return dbQuery {
            UserTable.selectAll().mapNotNull {
                rowToUser(it)
            }
        }
    }

    override suspend fun getUserById(userId: Int): UserModel? {
        return dbQuery {
            UserTable.select {
                UserTable.id.eq(userId)
            }.map {
                rowToUser(it)
            }.singleOrNull()
        }
    }


    private fun rowToUser(row: ResultRow?): UserModel? {
        if (row == null) {
            return null
        }

        return UserModel(
            id = row[UserTable.id],
            email = row[UserTable.email],
            password = row[UserTable.password],
            lastName = row[UserTable.lastName],
            firstName = row[UserTable.firstName],
            phoneNumber = row[UserTable.phoneNumber],
            telegramAccount = row[UserTable.telegramAccount],
            isActive = row[UserTable.isActive],
            role = row[UserTable.role].getRoleByString()
        )
    }


}