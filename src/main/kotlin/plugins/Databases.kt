package su.kawunprint.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import data.model.tables.PrinterHistoryTable
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import su.kawunprint.data.model.tables.*

object Databases {

    private val dotenv = dotenv()

    private val databaseUrl = dotenv["DB_POSTGRES_URL"]
    private val databaseUser = dotenv["DB_POSTGRES_USER"]
    private val databasePassword = dotenv["DB_PASSWORD"]

    fun Application.initDatabase() {
        Database.connect(getHikariDataSource())

        transaction {
            SchemaUtils.create(
                UserTable, FilamentTable, FilamentTypeTable, OrderTable, OrderHistoryTable, PrinterTable,
                OrderStatusTable, PrinterHistoryTable, OrderFileTable
            )
        }
    }

    private fun getHikariDataSource(): HikariDataSource {
        println("DB URL: $databaseUrl")
        println("DB user: $databaseUser")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = databaseUrl
            username = databaseUser
            password = databasePassword
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            transaction { block() }
        }
    }
}
