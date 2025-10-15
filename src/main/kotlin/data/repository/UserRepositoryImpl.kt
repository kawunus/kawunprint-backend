package su.kawunprint.data.repository

import data.model.UserModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
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
                table[UserTable.phone] = user.phoneNumber
                table[UserTable.role] = user.role.getStringByRole()
                table[UserTable.isActive] = user.isActive
            }
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
            phoneNumber = row[UserTable.phone],
            isActive = row[UserTable.isActive],
            role = row[UserTable.role].getRoleByString()
        )
    }


}