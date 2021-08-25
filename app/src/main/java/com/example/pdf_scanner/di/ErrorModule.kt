package com.example.pdf_scanner.di

import com.example.pdf_scanner.data.error.mapper.ErrorMapper
import com.example.pdf_scanner.data.error.mapper.ErrorMapperSource
import com.example.pdf_scanner.usecase.errors.ErrorManager
import com.example.pdf_scanner.usecase.errors.ErrorUseCase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// with @Module we Telling Dagger that, this is a Dagger module
@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {
    @Binds
    @Singleton
    abstract fun provideErrorFactoryImpl(errorManager: ErrorManager): ErrorUseCase

    @Binds
    @Singleton
    abstract fun provideErrorMapper(errorMapper: ErrorMapper): ErrorMapperSource
}
