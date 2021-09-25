package com.example.pdf_scanner.data

import kotlinx.coroutines.flow.Flow

interface DataRepositorySource {

    suspend fun requestStartCamera(): Flow<Resource<Boolean>>

    suspend fun cacheStartCamera(isEnabled: Boolean): Flow<Resource<Boolean>>

    suspend fun requestTextSize(): Flow<Resource<Int>>

    suspend fun cacheTextSize(size: Int): Flow<Resource<Boolean>>

    suspend fun requestLanguageOCR(): Flow<Resource<Set<String>>>

    suspend fun addLanguageOCR(language: String): Flow<Resource<Boolean>>

    suspend fun removeLanguageOCR(language: String): Flow<Resource<Boolean>>

    suspend fun saveLanguageOCR(languages: Set<String>): Flow<Resource<Boolean>>
}