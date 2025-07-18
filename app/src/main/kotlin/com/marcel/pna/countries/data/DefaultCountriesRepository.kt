package com.marcel.pna.countries.data

import com.marcel.pna.core.Result
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.Country
import com.marcel.pna.countries.domain.CountryError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DefaultCountriesRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: CountriesLocalDataSource,
    private val remoteDataSource: CountriesRemoteDataSource,
) : CountriesRepository {
    override fun getCountries(): Flow<List<Country>> {
        return localDataSource.getCountries()
            .flowOn(ioDispatcher)
    }

    override suspend fun updateCountries(): Result<CountryError, Unit> {
        return withContext(ioDispatcher) {
            remoteDataSource.getCountries()
                .map { countries ->
                    localDataSource.updateCountries(countries)
                }
        }

    }
}