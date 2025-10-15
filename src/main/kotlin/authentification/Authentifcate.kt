package su.kawunprint.authentification

import io.github.cdimascio.dotenv.dotenv
import io.ktor.util.*
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