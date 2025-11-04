package su.kawunprint.di

import domain.usecase.CartUseCase
import domain.usecase.OrderHistoryUseCase
import domain.usecase.OrderUseCase
import domain.usecase.PrinterUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import su.kawunprint.domain.usecase.FilamentTypeUseCase
import su.kawunprint.domain.usecase.FilamentUseCase
import su.kawunprint.domain.usecase.UserUseCase

val domainModule = module {
    factoryOf(::UserUseCase)

    factoryOf(::FilamentUseCase)

    factoryOf(::FilamentTypeUseCase)

    factoryOf(::PrinterUseCase)

    factoryOf(::CartUseCase)

    factoryOf(::OrderUseCase)

    factoryOf(::OrderHistoryUseCase)
}