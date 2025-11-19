package data.repository

import data.model.EmailVerificationCodeModel
import domain.repository.EmailVerificationRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import su.kawunprint.data.model.tables.EmailVerificationCodeTable
import su.kawunprint.plugins.Databases.dbQuery
import java.time.LocalDateTime

class EmailVerificationRepositoryImpl : EmailVerificationRepository {

    override suspend fun createCode(userId: Int, code: String, expiresAt: LocalDateTime): EmailVerificationCodeModel {
        return dbQuery {
            val id = EmailVerificationCodeTable.insert {
                it[EmailVerificationCodeTable.userId] = userId
                it[EmailVerificationCodeTable.code] = code
                it[EmailVerificationCodeTable.expiresAt] = expiresAt
                it[createdAt] = LocalDateTime.now()
            }[EmailVerificationCodeTable.id]

            EmailVerificationCodeModel(
                id = id,
                userId = userId,
                code = code,
                expiresAt = expiresAt,
                createdAt = LocalDateTime.now()
            )
        }
    }

    override suspend fun getValidCode(userId: Int, code: String): EmailVerificationCodeModel? {
        return dbQuery {
            EmailVerificationCodeTable
                .select {
                    (EmailVerificationCodeTable.userId eq userId) and
                            (EmailVerificationCodeTable.code eq code) and
                            (EmailVerificationCodeTable.expiresAt greater LocalDateTime.now())
                }
                .map { rowToModel(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteCodesForUser(userId: Int): Boolean {
        return dbQuery {
            EmailVerificationCodeTable.deleteWhere {
                EmailVerificationCodeTable.userId eq userId
            } > 0
        }
    }

    override suspend fun deleteExpiredCodes(): Int {
        return dbQuery {
            EmailVerificationCodeTable.deleteWhere {
                expiresAt less LocalDateTime.now()
            }
        }
    }

    private fun rowToModel(row: ResultRow): EmailVerificationCodeModel {
        return EmailVerificationCodeModel(
            id = row[EmailVerificationCodeTable.id],
            userId = row[EmailVerificationCodeTable.userId],
            code = row[EmailVerificationCodeTable.code],
            expiresAt = row[EmailVerificationCodeTable.expiresAt],
            createdAt = row[EmailVerificationCodeTable.createdAt]
        )
    }
}

