package su.kawunprint.authentification

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import data.model.UserModel
import io.github.cdimascio.dotenv.dotenv
import java.time.LocalDateTime
import java.time.ZoneOffset

class JwtService {
    private val dotenv = dotenv()

    private val issuer = "kawunprint-backend-server"
    private val jwtSecret = dotenv["JWT_SECRET"]
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    private val jwtVerifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: UserModel): String {
        return JWT.create()
            .withSubject("KawunPrintAuthentication")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withClaim("name", "${user.firstName} ${user.lastName}")
            .withClaim("role", user.role.name)
            .withClaim("id", user.id)
            .withClaim("isActive", user.isActive)
            .withExpiresAt(LocalDateTime.now().plusDays(8).toInstant(ZoneOffset.UTC))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier = jwtVerifier
}