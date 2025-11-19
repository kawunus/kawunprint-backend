package routes

import data.model.UserModel
import data.model.responses.AdminFileInfo
import data.model.responses.AdminFileListResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import su.kawunprint.authentification.authenticateWithRole
import su.kawunprint.data.model.RoleModel
import su.kawunprint.services.FirebaseStorageService

@Serializable
data class AdminFileUploadResponse(
    val success: Boolean,
    val fileName: String,
    val fileUrl: String,
    val storagePath: String,
    val fileSize: Int,
    val mimeType: String,
    val uploadedBy: Int,
    val uploadedAt: String
)

@Serializable
data class AdminFileDeleteResponse(
    val success: Boolean,
    val message: String,
    val path: String? = null
)

fun Route.adminFileRoute() {
    val firebaseStorageService: FirebaseStorageService by inject()

    authenticate("jwt") {
        route("/api/v1/admin/files") {

            // Admin file upload without restrictions
            post("/upload") {
                call.authenticateWithRole(RoleModel.ADMIN)

                val principal = call.principal<UserModel>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                try {
                    val multipart = call.receiveMultipart()
                    var fileBytes: ByteArray? = null
                    var fileName: String? = null
                    var mimeType = "application/octet-stream"
                    var customPath: String? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: "unnamed"
                                mimeType = part.contentType?.toString() ?: "application/octet-stream"
                                fileBytes = part.streamProvider().readBytes()
                            }

                            is PartData.FormItem -> {
                                if (part.name == "customPath") {
                                    customPath = part.value
                                }
                            }

                            else -> {}
                        }
                        part.dispose()
                    }

                    if (fileBytes == null || fileName == null) {
                        return@post call.respond(HttpStatusCode.BadRequest, "No file provided")
                    }

                    // Use custom path or default admin path
                    val timestamp = System.currentTimeMillis()
                    val storagePath = customPath?.let { path ->
                        val sanitizedPath = path.replace(Regex("[^a-zA-Z0-9._/-]"), "_")
                        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                        "$sanitizedPath/${timestamp}_$sanitizedFileName"
                    } ?: "admin/${timestamp}_${fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")}"

                    // Direct Firebase upload without restrictions
                    val (finalStoragePath, publicUrl) = firebaseStorageService.uploadFileAdmin(
                        fileBytes,
                        fileName,
                        mimeType,
                        storagePath
                    )

                    val response = AdminFileUploadResponse(
                        success = true,
                        fileName = fileName,
                        fileUrl = publicUrl,
                        storagePath = finalStoragePath,
                        fileSize = fileBytes.size,
                        mimeType = mimeType,
                        uploadedBy = principal.id,
                        uploadedAt = java.time.LocalDateTime.now().toString()
                    )

                    call.respond(HttpStatusCode.Created, response)

                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "File upload failed: ${e.message}")
                }
            }

            // Admin file deletion by storage path
            delete("/delete") {
                call.authenticateWithRole(RoleModel.ADMIN)

                val storagePath = call.request.queryParameters["path"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Storage path is required")

                try {
                    val deleted = firebaseStorageService.deleteFile(storagePath)
                    if (deleted) {
                        val response = AdminFileDeleteResponse(
                            success = true,
                            message = "File deleted successfully",
                            path = storagePath
                        )
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        val response = AdminFileDeleteResponse(
                            success = false,
                            message = "File not found or already deleted"
                        )
                        call.respond(HttpStatusCode.NotFound, response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Delete failed: ${e.message}")
                }
            }

            get("/list") {
                call.authenticateWithRole(RoleModel.ADMIN)

                val pathPrefix = call.request.queryParameters["prefix"] ?: "" // По умолчанию все файлы
                val maxResults = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100

                try {
                    val filesRaw = firebaseStorageService.listFiles(pathPrefix, maxResults)
                    val files = filesRaw.map { fileMap ->
                        AdminFileInfo(
                            name = fileMap["name"] as? String ?: "",
                            size = (fileMap["size"] as? Number)?.toLong(),
                            url = fileMap["url"] as? String
                        )
                    }
                    val response = AdminFileListResponse(
                        success = true,
                        prefix = pathPrefix.ifEmpty { "all" },
                        count = files.size,
                        files = files
                    )
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "List failed: ${e.message}")
                }
            }
        }
    }
}
