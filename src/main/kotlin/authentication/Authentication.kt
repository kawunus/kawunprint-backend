package su.kawunprint.authentification

import data.model.UserModel
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*
import su.kawunprint.data.model.RoleModel
import su.kawunprint.domain.exception.UnauthorizedException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val dotenv = dotenv()
private val hashKey = dotenv.get("HASH_SECRET_KEY").toByteArray()
private val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

fun hashPassword(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)

    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

fun ApplicationCall.authenticateWithRole(vararg targetRole: RoleModel) {
    principal<UserModel>().also {
        if (it == null || it.role !in targetRole) {
            throw UnauthorizedException()
        }
    }
}