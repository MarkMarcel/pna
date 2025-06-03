package com.marcel.pna.headlines

import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.PNAMDatabase
import com.marcel.pna.headlines.trending.countries.data.CountriesLocalDataSource
import com.marcel.pna.headlines.trending.countries.data.CountriesRemoteDataSource
import com.marcel.pna.headlines.trending.countries.data.DefaultCountriesRepository
import com.marcel.pna.headlines.trending.countries.data.RestCountriesApi
import com.marcel.pna.headlines.trending.countries.domain.CountriesRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val NEWS_API_RETROFIT = "newsApiRetrofit"
private const val REST_COUNTRIES_RETROFIT = "restCountriesRetrofit"

val HeadlinesModule = module {
    // CountriesLocalDataSource
    single { CountriesLocalDataSource(get(), get()) }
    // CountriesRemoteDataSource
    single { CountriesRemoteDataSource(get(), get()) }
    // CountriesRepository
    single<CountriesRepository> {
        DefaultCountriesRepository(
            get(named(IO_DISPATCHER)),
            get(),
            get()
        )
    }
    // CountriesRoomDao
    single { get<PNAMDatabase>().countriesRoomDao() }
    // News API retrofit client
    single(named(NEWS_API_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
    }
    // Rest Countries retrofit client
    single(named(REST_COUNTRIES_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
    }
    // RestCountriesApi
    single {
        get<Retrofit>(named(REST_COUNTRIES_RETROFIT)).create(RestCountriesApi::class.java)

    }
}