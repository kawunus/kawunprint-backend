package su.kawunprint.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import su.kawunprint.authentification.JwtService
import su.kawunprint.data.repository.FilamentRepositoryImpl
import su.kawunprint.data.repository.FilamentTypeRepositoryImpl
import su.kawunprint.data.repository.UserRepositoryImpl
import su.kawunprint.domain.repository.FilamentRepository
import su.kawunprint.domain.repository.FilamentTypeRepository
import su.kawunprint.domain.repository.UserRepository

val dataModule = module {
    singleOf(::JwtService)

    singleOf(::UserRepositoryImpl) bind UserRepository::class

    singleOf(::FilamentRepositoryImpl) bind FilamentRepository::class

    singleOf(::FilamentTypeRepositoryImpl) bind FilamentTypeRepository::class
}