package su.kawunprint.di

import org.koin.dsl.module
import su.kawunprint.authentification.JwtService
import su.kawunprint.data.repository.UserRepositoryImpl
import su.kawunprint.domain.repository.UserRepository

val dataModule = module {
    single { JwtService() }

    single<UserRepository> { UserRepositoryImpl() }
}