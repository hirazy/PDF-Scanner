package com.example.pdf_scanner.data

import com.example.pdf_scanner.KEY_LANGUAGE_OCR
import com.example.pdf_scanner.START_CAMERA
import com.example.pdf_scanner.data.local.AssetData
import com.example.pdf_scanner.data.local.LocalData
import com.example.pdf_scanner.data.remote.RemoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val assetRepository: AssetData,
    private val ioDispatcher: CoroutineContext
): DataRepositorySource{

    override suspend fun requestStartCamera(): Flow<Resource<Boolean>> {
        return flow{
            emit(localRepository.getCacheSettingsDefaultFalse(START_CAMERA))
        }.flowOn(ioDispatcher)
    }

    override suspend fun cacheStartCamera(isEnabled: Boolean): Flow<Resource<Boolean>> {
        return flow{
            emit(localRepository.cacheStartCamera(isEnabled))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestTextSize(): Flow<Resource<Int>> {
        return flow {
            emit(localRepository.getCacheTextSize())
        }.flowOn(ioDispatcher)
    }

    override suspend fun cacheTextSize(size: Int): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.cacheTextSize(size))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestLanguageOCR(): Flow<Resource<Set<String>>> {
        return flow {
            emit(localRepository.getCacheLanguageOCR())
        }.flowOn(ioDispatcher)
    }

    override suspend fun addLanguageOCR(language: String): Flow<Resource<Boolean>> {
        return flow{
            localRepository.getCacheLanguageOCR().let {
                it.data!!.toMutableSet().let { set->
                    val isAdded = set.add(language)
                    if (isAdded) {
                        emit(localRepository.cacheLanguageOCR(set))
                    } else {
                        emit(Resource.Success(false))
                    }
                }
                it.errorCode.let { errorCode ->
                    emit(Resource.DataError<Boolean>(errorCode!!))
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun removeLanguageOCR(language: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.removeLanguageOCR(language))
        }.flowOn(ioDispatcher)
    }

    override suspend fun saveLanguageOCR(languages: Set<String>): Flow<Resource<Boolean>> {
        return flow{
            emit(localRepository.cacheLanguageOCR(languages))
        }.flowOn(ioDispatcher)
    }

}