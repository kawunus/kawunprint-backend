package test

import su.kawunprint.services.FirebaseStorageService

fun main() {
    println("🧪 Тест инициализации Firebase Storage...")

    try {
        val firebaseService = FirebaseStorageService()

        println("\n📊 РЕЗУЛЬТАТЫ ТЕСТА:")
        println("Firebase включен: ${firebaseService.isFirebaseEnabled()}")
        println("Bucket: ${firebaseService.getBucketName()}")

    } catch (e: Exception) {
        println("❌ Ошибка теста: ${e.message}")
        e.printStackTrace()
    }
}
