package com.marcel.pna.countries

import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.PNAMDatabase
import com.marcel.pna.countries.data.CountriesLocalDataSource
import com.marcel.pna.countries.data.CountriesRemoteDataSource
import com.marcel.pna.countries.data.DefaultCountriesRepository
import com.marcel.pna.countries.data.RestCountriesApi
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.usecases.CountriesUseCaseProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val REST_COUNTRIES_RETROFIT = "restCountriesRetrofit"

val CountriesModule = module {
    single(named(REST_COUNTRIES_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
            .build()
    }

    single { get<PNAMDatabase>().countriesRoomDao() }

    single {
        get<Retrofit>(named(REST_COUNTRIES_RETROFIT)).create(RestCountriesApi::class.java)
    }

    single {
        CountriesLocalDataSource(
            countriesRoomDao = get(),
            logger = get()
        )
    }

    single {
        CountriesRemoteDataSource(
            api = get(),
            logger = get()
        )
    }

    single<CountriesRepository> {
        DefaultCountriesRepository(
            ioDispatcher = get(named(IO_DISPATCHER)),
            localDataSource = get(),
            remoteDataSource = get()
        )
    }

    single {
        CountriesUseCaseProvider(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            countriesRepository = get()
        )
    }

}