package su.kawunprint.utils

object FileUtils {

    // Разрешенные MIME типы для загрузки
    val ALLOWED_MIME_TYPES = setOf(
        // Изображения
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp",

        // 3D модели
        "application/octet-stream", // STL файлы обычно имеют этот тип
        "model/stl",
        "application/sla", // Для SLA принтеров
        "text/plain", // G-code файлы

        // Документы
        "application/pdf",
        "text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",

        // Архивы
        "application/zip",
        "application/x-rar-compressed",
        "application/x-7z-compressed"
    )

    // Разрешенные расширения файлов
    val ALLOWED_EXTENSIONS = setOf(
        // Изображения
        "jpg", "jpeg", "png", "gif", "webp", "bmp",

        // 3D модели
        "stl", "obj", "ply", "3mf", "amf", "gcode", "sla",

        // Документы
        "pdf", "txt", "doc", "docx", "xls", "xlsx",

        // Архивы
        "zip", "rar", "7z"
    )

    /**
     * Проверяет, разрешен ли данный файл для загрузки
     */
    fun isAllowedFile(fileName: String, mimeType: String): Boolean {
        // Проверяем MIME тип
        if (mimeType in ALLOWED_MIME_TYPES) return true

        // Проверяем расширение как fallback
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in ALLOWED_EXTENSIONS
    }

    /**
     * Форматирует размер файла в человекочитаемый вид
     */
    fun formatFileSize(sizeInBytes: Long): String {
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
     * Проверяет, является ли файл изображением
     */
    fun isImage(mimeType: String): Boolean {
        return mimeType.startsWith("image/")
    }

    /**
     * Генерирует безопасное имя файла
     */
    fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(200) // Ограничиваем длину
    }
}
