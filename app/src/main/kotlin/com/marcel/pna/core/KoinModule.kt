package com.marcel.pna.core

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.converter.moshi.MoshiConverterFactory

const val BACKGROUND_DISPATCHER = "backgroundDispatcher"
const val IO_DISPATCHER = "ioDispatcher"

val CoreModule = module {
    // App Logger
    single { Logger() }
    // Background dispatcher
    single(named(BACKGROUND_DISPATCHER)) {
        Dispatchers.Default
    }
    // Database
    single<PNAMDatabase> {
        Room.databaseBuilder(
            androidContext(),
            PNAMDatabase::class.java,
            "pna-m-database"
        ).build()
    }
    // Http logger
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS // Logs headers
        }
    }
    // IO dispatcher
    single(named(IO_DISPATCHER)) {
        Dispatchers.IO
    }
    // Moshi retrofit converter
    single<MoshiConverterFactory> {
        MoshiConverterFactory.create()
    }
}