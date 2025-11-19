package routes

import data.model.UserModel
import data.model.responses.FileInfoResponse
import data.model.responses.FileStatsResponse
import domain.usecase.OrderFileUseCase
import domain.usecase.OrderUseCase
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import su.kawunprint.data.model.RoleModel

fun Route.orderFileRoute() {
    val orderFileUseCase: OrderFileUseCase by inject()
    val orderUseCase: OrderUseCase by inject()

    authenticate("jwt") {
        route("/api/v1/orders/{orderId}/files") {

            // Get all files for an order
            get {
                val principal = call.principal<UserModel>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

                // Check if order exists
                val order = orderUseCase.getOrderById(orderId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Order not found")

                // Check permissions: owner, admin, employee, or analyst can view files
                if (order.customer.id != principal.id &&
                    principal.role != RoleModel.ADMIN &&
                    principal.role != RoleModel.EMPLOYEE &&
                    principal.role != RoleModel.ANALYST
                ) {
                    return@get call.respond(HttpStatusCode.Forbidden, "You can only view files of your own orders")
                }

                val files = orderFileUseCase.getFilesByOrderId(orderId)
                call.respond(HttpStatusCode.OK, files)
            }

            // Upload file to order
            post {
                val principal = call.principal<UserModel>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

                // Check if order exists
                val order = orderUseCase.getOrderById(orderId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Order not found")

                // Check permissions: only order owner, admin or employee can upload
                if (order.customer.id != principal.id &&
                    principal.role != RoleModel.ADMIN &&
                    principal.role != RoleModel.EMPLOYEE
                ) {
                    return@post call.respond(HttpStatusCode.Forbidden, "You can only upload files to your own orders")
                }

                // Check if can upload more files
                if (!orderFileUseCase.canUploadMoreFiles(orderId)) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Максимум ${OrderFileUseCase.MAX_FILES_PER_ORDER} файла на заказ"
                    )
                }

                try {
                    val multipart = call.receiveMultipart()
                    var fileBytes: ByteArray? = null
                    var fileName: String? = null
                    var mimeType = "application/octet-stream"

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: "unnamed"
                                mimeType = part.contentType?.toString() ?: "application/octet-stream"
                                fileBytes = part.streamProvider().readBytes()
                            }

                            else -> {}
                        }
                        part.dispose()
                    }

                    if (fileBytes == null || fileName == null) {
                        return@post call.respond(HttpStatusCode.BadRequest, "No file provided")
                    }

                    val uploadedFile = orderFileUseCase.uploadFile(
                        orderId = orderId,
                        fileBytes = fileBytes,
                        fileName = fileName,
                        mimeType = mimeType,
                        uploadedBy = principal.id
                    )

                    call.respond(HttpStatusCode.Created, uploadedFile)

                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Upload failed")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "File upload failed")
                }
            }

            // Delete file
            delete("/{fileId}") {
                val principal = call.principal<UserModel>()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

                val fileId = call.parameters["fileId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid file ID")

                // Check if file exists and belongs to the order
                val file = orderFileUseCase.getFileById(fileId)
                    ?: return@delete call.respond(HttpStatusCode.NotFound, "File not found")

                if (file.orderId != orderId) {
                    return@delete call.respond(HttpStatusCode.BadRequest, "File does not belong to this order")
                }

                // Check permissions: only uploader, admin or employee can delete
                val order = orderUseCase.getOrderById(orderId)
                    ?: return@delete call.respond(HttpStatusCode.NotFound, "Order not found")

                if (file.uploadedBy != principal.id &&
                    order.customer.id != principal.id &&
                    principal.role != RoleModel.ADMIN &&
                    principal.role != RoleModel.EMPLOYEE
                ) {
                    return@delete call.respond(HttpStatusCode.Forbidden)
                }

                val deleted = orderFileUseCase.deleteFile(fileId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "File deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete file")
                }
            }

            // Get file upload statistics for order
            get("/stats") {
                val principal = call.principal<UserModel>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

                try {
                    // Check if order exists
                    val order = orderUseCase.getOrderById(orderId)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Order not found")

                    // Check permissions: owner, admin, employee, or analyst can view stats
                    if (order.customer.id != principal.id &&
                        principal.role != RoleModel.ADMIN &&
                        principal.role != RoleModel.EMPLOYEE &&
                        principal.role != RoleModel.ANALYST
                    ) {
                        return@get call.respond(HttpStatusCode.Forbidden, "You can only view stats of your own orders")
                    }

                    val files = orderFileUseCase.getFilesByOrderId(orderId)
                    val totalSize = files.sumOf { it.fileSize }
                    val canUploadMore = orderFileUseCase.canUploadMoreFiles(orderId)
                    val stats = FileStatsResponse(
                        orderId = orderId,
                        totalFiles = files.size,
                        maxFiles = OrderFileUseCase.MAX_FILES_PER_ORDER,
                        remainingSlots = OrderFileUseCase.MAX_FILES_PER_ORDER - files.size,
                        totalSize = totalSize,
                        totalSizeFormatted = su.kawunprint.utils.FileUtils.formatFileSize(totalSize),
                        maxFileSize = OrderFileUseCase.MAX_FILE_SIZE,
                        maxFileSizeFormatted = su.kawunprint.utils.FileUtils.formatFileSize(OrderFileUseCase.MAX_FILE_SIZE.toLong()),
                        canUploadMore = canUploadMore,
                        files = files.map { file ->
                            FileInfoResponse(
                                id = file.id,
                                fileName = file.fileName,
                                size = file.fileSize,
                                sizeFormatted = su.kawunprint.utils.FileUtils.formatFileSize(file.fileSize),
                                mimeType = file.mimeType,
                                isImage = su.kawunprint.utils.FileUtils.isImage(file.mimeType),
                                uploadedAt = file.uploadedAt
                            )
                        }
                    )
                    call.respond(HttpStatusCode.OK, stats)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Failed to get file statistics")
                }
            }
        }
    }
}
