package su.kawunprint.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.StorageClient
import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class FirebaseStorageService {

    private val dotenv = dotenv()
    private val bucketName: String = dotenv["FIREBASE_STORAGE_BUCKET"] ?: "default-bucket"
    private val serviceAccountPath: String? = dotenv["FIREBASE_SERVICE_ACCOUNT_PATH"]

    // Fallback to local storage if Firebase not configured
    private val useFirebase: Boolean
    private val localStoragePath = "./uploads"

    // Public methods to check status
    fun isFirebaseEnabled(): Boolean = useFirebase
    fun getBucketName(): String = bucketName

    init {
        println("🔥 Инициализация Firebase Storage...")
        println("📋 Bucket: $bucketName")
        println("🔑 Service Account Path: ${serviceAccountPath ?: "НЕ ЗАДАН"}")

        useFirebase = try {
            initializeFirebase()
            println("✅ Firebase Storage успешно подключен!")
            true
        } catch (e: Exception) {
            println("⚠️ Firebase не настроен, используем локальное хранилище")
            println("💡 Причина: ${e.message}")
            println("📁 Локальная папка: $localStoragePath")
            File(localStoragePath).mkdirs()
            false
        }

        println("🎯 Режим хранения: ${if (useFirebase) "Firebase Storage" else "Локальное хранилище"}")
        println("=" + "=".repeat(50))
    }

    private fun initializeFirebase() {
        println("🔍 Проверка настроек Firebase...")

        if (serviceAccountPath.isNullOrEmpty()) {
            throw IllegalStateException("FIREBASE_SERVICE_ACCOUNT_PATH не указан в .env файле")
        }

        val serviceAccountFile = File(serviceAccountPath)
        println("📄 Проверка JSON ключа: $serviceAccountPath")

        if (!serviceAccountFile.exists()) {
            throw IllegalStateException("JSON ключ не найден по пути: $serviceAccountPath")
        }

        if (!serviceAccountFile.canRead()) {
            throw IllegalStateException("Нет прав на чтение JSON ключа: $serviceAccountPath")
        }

        println("✅ JSON ключ найден и доступен для чтения")
        println("📊 Размер ключа: ${serviceAccountFile.length()} байт")

        if (FirebaseApp.getApps().isEmpty()) {
            println("🚀 Инициализация Firebase Admin SDK...")

            val serviceAccount = FileInputStream(serviceAccountPath)
            val credentials = GoogleCredentials.fromStream(serviceAccount)

            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setStorageBucket(bucketName)
                .build()

            FirebaseApp.initializeApp(options)
            println("✅ Firebase Admin SDK инициализирован")
        } else {
            println("ℹ️ Firebase Admin SDK уже инициализирован")
        }

        println("🎯 Подключение к Storage bucket: $bucketName")
    }

    /**
     * Upload file to Firebase Storage or local storage
     */
    fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        orderId: Int
    ): Pair<String, String> {
        val timestamp = System.currentTimeMillis()
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val uniqueFileName = "${orderId}_${timestamp}_${sanitizedFileName}"
        val storagePath = "orders/$orderId/$uniqueFileName"

        println("📤 Загрузка файла: $fileName (${formatFileSize(fileBytes.size.toLong())})")
        println("🎯 Путь: $storagePath")
        println("🔧 Метод: ${if (useFirebase) "Firebase Storage" else "Локальное хранилище"}")

        return if (useFirebase) {
            uploadToFirebase(fileBytes, storagePath, mimeType)
        } else {
            uploadToLocal(fileBytes, storagePath)
        }
    }

    /**
     * Admin upload with custom path
     */
    fun uploadFileAdmin(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        customStoragePath: String
    ): Pair<String, String> {
        return if (useFirebase) {
            uploadToFirebase(fileBytes, customStoragePath, mimeType)
        } else {
            uploadToLocal(fileBytes, customStoragePath)
        }
    }

    private fun uploadToFirebase(fileBytes: ByteArray, storagePath: String, mimeType: String): Pair<String, String> {
        try {
            println("🔥 Загрузка в Firebase Storage...")
            val bucket = StorageClient.getInstance().bucket()
            println("✅ Подключение к bucket успешно")

            val blob = bucket.create(storagePath, fileBytes, mimeType)
            println("✅ Файл загружен в Firebase: $storagePath")

            // Generate signed URL valid for 7 days
            val signedUrl = blob.signUrl(7, TimeUnit.DAYS).toString()
            println("🔗 Создан signed URL (действует 7 дней)")

            return Pair(storagePath, signedUrl)
        } catch (e: Exception) {
            println("❌ Ошибка загрузки в Firebase: ${e.javaClass.simpleName}")
            println("💬 Сообщение: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun uploadToLocal(fileBytes: ByteArray, storagePath: String): Pair<String, String> {
        val fullPath = "$localStoragePath/$storagePath"
        val file = File(fullPath)

        println("💾 Сохранение в локальное хранилище...")
        println("📁 Путь: $fullPath")

        file.parentFile.mkdirs()
        Files.write(Paths.get(fullPath), fileBytes)

        val publicUrl = "http://localhost:8080/uploads/$storagePath"
        println("✅ Файл сохранен локально")
        println("🔗 URL: $publicUrl")

        return Pair(storagePath, publicUrl)
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            sizeInBytes >= gb -> "%.2f GB".format(sizeInBytes.toDouble() / gb)
            sizeInBytes >= mb -> "%.2f MB".format(sizeInBytes.toDouble() / mb)
            sizeInBytes >= kb -> "%.2f KB".format(sizeInBytes.toDouble() / kb)
            else -> "$sizeInBytes bytes"
        }
    }

    /**
     * Delete file
     */
    fun deleteFile(storagePath: String): Boolean {
        return if (useFirebase) {
            try {
                val bucket = StorageClient.getInstance().bucket()
                bucket.get(storagePath)?.delete() ?: false
            } catch (e: Exception) {
                println("❌ Ошибка удаления из Firebase: ${e.message}")
                false
            }
        } else {
            try {
                File("$localStoragePath/$storagePath").delete()
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Get file URL
     */
    fun getFileUrl(storagePath: String): String? {
        return if (useFirebase) {
            try {
                val bucket = StorageClient.getInstance().bucket()
                val blob = bucket.get(storagePath)
                blob?.signUrl(7, TimeUnit.DAYS)?.toString()
            } catch (e: Exception) {
                null
            }
        } else {
            val file = File("$localStoragePath/$storagePath")
            if (file.exists()) "http://localhost:8080/uploads/$storagePath" else null
        }
    }

    /**
     * Check if file exists
     */
    fun fileExists(storagePath: String): Boolean {
        return if (useFirebase) {
            try {
                val bucket = StorageClient.getInstance().bucket()
                bucket.get(storagePath) != null
            } catch (e: Exception) {
                false
            }
        } else {
            File("$localStoragePath/$storagePath").exists()
        }
    }

    /**
     * List files with prefix
     */
    fun listFiles(pathPrefix: String, maxResults: Int = 100): List<Map<String, Any>> {
        return if (useFirebase) {
            try {
                val bucket = StorageClient.getInstance().bucket()
                val blobs = bucket.list(
                    com.google.cloud.storage.Storage.BlobListOption.prefix(pathPrefix),
                    com.google.cloud.storage.Storage.BlobListOption.pageSize(maxResults.toLong())
                )

                blobs.iterateAll().take(maxResults).map { blob ->
                    mapOf(
                        "name" to blob.name,
                        "size" to blob.size,
                        "contentType" to (blob.contentType ?: "unknown"),
                        "created" to blob.createTime.toString(),
                        "updated" to blob.updateTime.toString(),
                        "url" to blob.signUrl(7, TimeUnit.DAYS).toString()
                    )
                }.toList()
            } catch (e: Exception) {
                println("❌ Ошибка получения списка файлов: ${e.message}")
                emptyList()
            }
        } else {
            try {
                val directory = File("$localStoragePath/$pathPrefix")
                if (!directory.exists()) return emptyList()

                directory.walkTopDown()
                    .filter { it.isFile }
                    .take(maxResults)
                    .map { file ->
                        val relativePath = file.relativeTo(File(localStoragePath)).path.replace(File.separator, "/")
                        mapOf(
                            "name" to relativePath,
                            "size" to file.length(),
                            "contentType" to "unknown",
                            "created" to java.time.Instant.ofEpochMilli(file.lastModified()).toString(),
                            "updated" to java.time.Instant.ofEpochMilli(file.lastModified()).toString(),
                            "url" to "http://localhost:8080/uploads/$relativePath"
                        )
                    }.toList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
