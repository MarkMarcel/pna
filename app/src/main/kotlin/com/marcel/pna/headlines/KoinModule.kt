package com.marcel.pna.headlines

import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.PNAMDatabase
import com.marcel.pna.countries.data.CountriesLocalDataSource
import com.marcel.pna.countries.data.CountriesRemoteDataSource
import com.marcel.pna.countries.data.DefaultCountriesRepository
import com.marcel.pna.countries.data.RestCountriesApi
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.usecases.CountriesUseCaseProvider
import com.marcel.pna.headlines.data.ArticleLocalDataSource
import com.marcel.pna.headlines.data.DefaultHeadlinesRepository
import com.marcel.pna.headlines.data.HeadlinesRemoteDataSource
import com.marcel.pna.headlines.domain.HeadlinesRepository
import com.marcel.pna.headlines.data.HeadlinesApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val NEWS_API_RETROFIT = "newsApiRetrofit"
private const val REST_COUNTRIES_RETROFIT = "restCountriesRetrofit"

val HeadlinesModule = module {
    single { ArticleLocalDataSource(get(), get()) }

    single { get<PNAMDatabase>().articleRoomDao() }

    single { get<PNAMDatabase>().articleRoomDao() }

    single { CountriesLocalDataSource(get(), get()) }

    single { CountriesRemoteDataSource(get(), get()) }

    single<CountriesRepository> {
        DefaultCountriesRepository(
            get(named(IO_DISPATCHER)),
            get(),
            get()
        )
    }

    single {
        CountriesUseCaseProvider(get(named(BACKGROUND_DISPATCHER)), get())
    }

    single { get<PNAMDatabase>().countriesRoomDao() }

    single {
        HeadlinesRemoteDataSource(
            appConfigProvider = get(),
            api = get(),
            logger = get(),
            moshi = get()
        )
    }

    single<HeadlinesRepository> {
        DefaultHeadlinesRepository(
            get(named(IO_DISPATCHER)),
            get(),
            get()
        )
    }
    // News API retrofit client
    // Todo: add interceptor to track number of requests in a day
    single(named(NEWS_API_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
            .build()
    }
    // Rest Countries retrofit client
    single(named(REST_COUNTRIES_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
            .build()
    }

    single {
        get<Retrofit>(named(REST_COUNTRIES_RETROFIT)).create(RestCountriesApi::class.java)

    }

    single {
        get<Retrofit>(named(NEWS_API_RETROFIT)).create(HeadlinesApi::class.java)
    }
}