package su.kawunprint.di

import org.koin.dsl.module
import su.kawunprint.domain.usecase.UserUseCase

val domainModule = module {
    factory { UserUseCase(get(), get()) }
}