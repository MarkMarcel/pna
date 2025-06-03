package com.marcel.pna.headlines.trending.countries.data

import com.marcel.pna.core.Logger
import com.marcel.pna.headlines.trending.countries.domain.Country
import com.marcel.pna.headlines.trending.countries.domain.toDatabaseModel
import com.marcel.pna.headlines.trending.countries.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class CountriesLocalDataSource(
    private val countriesRoomDao: CountriesRoomDao,
    private val logger: Logger,
) {
    fun getCountries(): Flow<List<Country>> {
        return countriesRoomDao.getCountries()
            .map { countryDatabaseModels ->
                countryDatabaseModels.map { countryDatabaseModel ->
                    countryDatabaseModel.toDomain()
                }
            }
            .catch {
                logger.logError(it)
            }
    }

    suspend fun updateCountries(countries: List<Country>) {
        countriesRoomDao.insertCountries(countries.map { it.toDatabaseModel() })
    }
}