package su.kawunprint.di

import data.repository.CartRepositoryImpl
import data.repository.OrderHistoryRepositoryImpl
import data.repository.PrinterRepositoryImpl
import data.repository.UserRepositoryImpl
import domain.repository.CartRepository
import domain.repository.OrderHistoryRepository
import domain.repository.OrderRepository
import domain.repository.PrinterRepository
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

val dataModule = module {
    singleOf(::JwtService)

    singleOf(::UserRepositoryImpl) bind UserRepository::class

    singleOf(::FilamentRepositoryImpl) bind FilamentRepository::class

    singleOf(::FilamentTypeRepositoryImpl) bind FilamentTypeRepository::class

    singleOf(::PrinterRepositoryImpl) bind PrinterRepository::class

    singleOf(::CartRepositoryImpl) bind CartRepository::class

    singleOf(::OrderRepositoryImpl) bind OrderRepository::class

    singleOf(::OrderHistoryRepositoryImpl) bind OrderHistoryRepository::class
}