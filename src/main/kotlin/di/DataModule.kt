package su.kawunprint.di

import data.repository.*
import domain.repository.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import su.kawunprint.authentification.JwtService
import su.kawunprint.data.repository.FilamentRepositoryImpl
import su.kawunprint.data.repository.FilamentTypeRepositoryImpl
import su.kawunprint.data.repository.OrderRepositoryImpl
import su.kawunprint.domain.repository.FilamentRepository
import su.kawunprint.domain.repository.FilamentTypeRepository
import su.kawunprint.domain.repository.UserRepository
import su.kawunprint.services.FirebaseStorageService

val dataModule = module {
    singleOf(::JwtService)

    // Eager singleton to initialize Firebase at startup
    single(createdAtStart = true) { FirebaseStorageService() }

    singleOf(::UserRepositoryImpl) bind UserRepository::class

    singleOf(::FilamentRepositoryImpl) bind FilamentRepository::class

    singleOf(::FilamentTypeRepositoryImpl) bind FilamentTypeRepository::class

    singleOf(::PrinterRepositoryImpl) bind PrinterRepository::class

    singleOf(::OrderRepositoryImpl) bind OrderRepository::class

    singleOf(::OrderHistoryRepositoryImpl) bind OrderHistoryRepository::class

    singleOf(::OrderStatusRepositoryImpl) bind OrderStatusRepository::class

    singleOf(::PrinterHistoryRepositoryImpl) bind PrinterHistoryRepository::class

    singleOf(::OrderFileRepositoryImpl) bind OrderFileRepository::class
}