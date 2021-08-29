package com.example.pdf_scanner.di

import android.content.Context
import com.example.pdf_scanner.data.local.AssetData
import com.example.pdf_scanner.data.local.LocalData
import com.example.pdf_scanner.utils.*
import com.oneadx.vpnclient.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext context: Context,
        sharedPrefsSource: SharedPrefsSource
    ): LocalData {
        return LocalData(context, sharedPrefsSource)
    }

    @Provides
    @Singleton
    fun provideAssetRepository(
        @ApplicationContext context: Context
    ): AssetData {
        return AssetData(context)
    }

    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return Network(context)
    }

    @Provides
    @Singleton
    fun provideDownload(@ApplicationContext context: Context): Download {
        return Download(context)
    }

    @Provides
    @Singleton
    fun provideSharedPrefsSource(@ApplicationContext context: Context): SharedPrefsSource {
        return SharedPrefs(context)
    }

    @Provides
    @Singleton
    fun provideFileSource(@ApplicationContext context: Context): FileSource {
        return FileUtil(context)
    }
}