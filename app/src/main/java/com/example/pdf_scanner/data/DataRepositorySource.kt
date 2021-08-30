package com.example.pdf_scanner.data

import kotlinx.coroutines.flow.Flow

interface DataRepositorySource {
    suspend fun requestLanguageOCR(): Flow<Resource<Set<String>>>

    suspend fun addLanguageOCR(language: String): Flow<Resource<Boolean>>

    suspend fun removeLanguageOCR(language: String): Flow<Resource<Boolean>>

    suspend fun saveLanguageOCR(languages: Set<String>): Flow<Resource<Boolean>>
}