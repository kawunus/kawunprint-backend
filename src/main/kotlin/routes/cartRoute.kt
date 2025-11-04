package routes

import data.model.CartModel
import data.model.requests.cart.CreateCartItemRequest
import data.model.requests.cart.UpdateCartItemRequest
import domain.usecase.CartUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.domain.usecase.FilamentUseCase
import su.kawunprint.domain.usecase.UserUseCase
import java.time.LocalDateTime

fun Route.cartRoute() {
    val cartUseCase: CartUseCase by inject()
    val filamentUseCase: FilamentUseCase by inject()
    val userUseCase: UserUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/cart") {

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                val cartItems = cartUseCase.getCartItemsByUserId(userId)
                call.respond(HttpStatusCode.OK, cartItems)
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                val user = userUseCase.getUserById(userId) ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val body = call.receive<CreateCartItemRequest>()
                val filament = body.filamentId?.let { filamentUseCase.getFilamentById(it) }

                if (body.filamentId != null && filament == null) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Invalid filament ID")
                }

                val cartItem = CartModel(
                    user = user,
                    filament = filament,
                    filePath = body.filePath,
                    weight = body.weight,
                    estimatedPrice = body.estimatedPrice,
                    comment = body.comment,
                    createdAt = LocalDateTime.now()
                )

                val createdItem = cartUseCase.createCartItem(cartItem)
                if (createdItem != null) {
                    call.respond(HttpStatusCode.Created, createdItem)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()

                val existingItem = cartUseCase.getCartItemById(id)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                if (existingItem.user.id != userId) {
                    return@put call.respond(HttpStatusCode.Forbidden)
                }

                val body = call.receive<UpdateCartItemRequest>()
                val filament = body.filamentId?.let { filamentUseCase.getFilamentById(it) }

                if (body.filamentId != null && filament == null) {
                    return@put call.respond(HttpStatusCode.BadRequest, "Invalid filament ID")
                }

                val updatedItem = existingItem.copy(
                    filament = filament,
                    filePath = body.filePath,
                    weight = body.weight,
                    estimatedPrice = body.estimatedPrice,
                    comment = body.comment,
                )

                cartUseCase.updateCartItem(updatedItem)
                call.respond(HttpStatusCode.OK, updatedItem)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()

                val existingItem = cartUseCase.getCartItemById(id)
                    ?: return@delete call.respond(HttpStatusCode.NotFound)

                if (existingItem.user.id != userId) {
                    return@delete call.respond(HttpStatusCode.Forbidden)
                }

                cartUseCase.deleteCartItem(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}