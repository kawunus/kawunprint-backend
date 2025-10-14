package su.kawunprint

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import io.github.cdimascio.dotenv.dotenv

object DatabaseFactory {

    private val dotenv = dotenv()

    private val databaseUrl = dotenv["DB_POSTGRES_URL"]
    private val databaseUser = dotenv["DB_POSTGRES_USER"]
    private val databasePassword = dotenv["DB_PASSWORD"]

    fun Application.initDatabase() {
        Database.connect(getHikariDataSource())
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
}
