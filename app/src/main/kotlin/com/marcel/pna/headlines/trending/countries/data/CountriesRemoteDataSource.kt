package com.marcel.pna.headlines.trending.countries.data

import com.marcel.pna.core.Logger
import com.marcel.pna.core.Result
import com.marcel.pna.headlines.trending.countries.data.models.toDomain
import com.marcel.pna.headlines.trending.countries.domain.Country
import com.marcel.pna.headlines.trending.countries.domain.CountryError
import retrofit2.HttpException
import java.io.IOException

class CountriesRemoteDataSource(
    private val api: RestCountriesApi,
    private val logger: Logger,
) {
    suspend fun getCountries(): Result<CountryError, List<Country>> {
        return Result.catching(
            delayMillisProvider = { attempt -> 1000L * attempt },
            shouldRetryProvider = { attempt, cause -> attempt <= 3 && cause is IOException }
        ) {
            api.getCountries().map { it.toDomain() }
        }.mapFailure {
            logger.logError(it)
            when (it) {
                is HttpException -> CountryError.Server
                is IOException -> CountryError.Network
                else -> CountryError.Server
            }
        }
    }
}