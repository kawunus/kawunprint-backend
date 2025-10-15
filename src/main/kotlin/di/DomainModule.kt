package su.kawunprint.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import su.kawunprint.domain.usecase.FilamentTypeUseCase
import su.kawunprint.domain.usecase.FilamentUseCase
import su.kawunprint.domain.usecase.UserUseCase

val domainModule = module {
    factoryOf(::UserUseCase)

    factoryOf(::FilamentUseCase)

    factoryOf(::FilamentTypeUseCase)
}