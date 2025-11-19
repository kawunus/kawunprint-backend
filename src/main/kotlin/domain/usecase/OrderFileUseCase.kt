package domain.usecase

import data.model.OrderFileModel
import domain.repository.OrderFileRepository
import su.kawunprint.services.FirebaseStorageService
import su.kawunprint.utils.FileUtils
import java.time.LocalDateTime

class OrderFileUseCase(
    private val orderFileRepository: OrderFileRepository,
    private val firebaseStorageService: FirebaseStorageService
) {
    companion object {
        const val MAX_FILES_PER_ORDER = 10
        const val MAX_FILE_SIZE = 100 * 1024 * 1024 // 100MB
    }

    suspend fun uploadFile(
        orderId: Int,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        uploadedBy: Int
    ): OrderFileModel {
        // Validate file count
        val currentFileCount = orderFileRepository.countFilesByOrderId(orderId)
        if (currentFileCount >= MAX_FILES_PER_ORDER) {
            throw IllegalStateException("Максимум $MAX_FILES_PER_ORDER файлов на заказ")
        }

        // Validate file size
        if (fileBytes.size > MAX_FILE_SIZE) {
            throw IllegalStateException("Размер файла не должен превышать ${FileUtils.formatFileSize(MAX_FILE_SIZE.toLong())}")
        }

        // Validate file type
        if (!FileUtils.isAllowedFile(fileName, mimeType)) {
            throw IllegalStateException("Тип файла не поддерживается. Разрешенные типы: изображения, 3D модели, документы, архивы")
        }

        // Upload to Firebase
        val (storagePath, publicUrl) = firebaseStorageService.uploadFile(
            fileBytes, fileName, mimeType, orderId
        )

        // Save metadata to DB
        val fileModel = OrderFileModel(
            orderId = orderId,
            fileName = fileName,
            fileUrl = publicUrl,
            firebaseStoragePath = storagePath,
            fileSize = fileBytes.size.toLong(),
            mimeType = mimeType,
            uploadedAt = LocalDateTime.now(),
            uploadedBy = uploadedBy
        )

        return orderFileRepository.saveFileMetadata(fileModel)
            ?: throw IllegalStateException("Failed to save file metadata")
    }

    suspend fun getFilesByOrderId(orderId: Int): List<OrderFileModel> {
        return orderFileRepository.getFilesByOrderId(orderId)
    }

    suspend fun getFileById(fileId: Int): OrderFileModel? {
        return orderFileRepository.getFileById(fileId)
    }

    suspend fun deleteFile(fileId: Int): Boolean {
        val file = orderFileRepository.getFileById(fileId) ?: return false

        // Delete from Firebase Storage
        firebaseStorageService.deleteFile(file.firebaseStoragePath)

        // Delete metadata from DB
        return orderFileRepository.deleteFile(fileId)
    }

    suspend fun canUploadMoreFiles(orderId: Int): Boolean {
        val currentCount = orderFileRepository.countFilesByOrderId(orderId)
        return currentCount < MAX_FILES_PER_ORDER
    }
}
