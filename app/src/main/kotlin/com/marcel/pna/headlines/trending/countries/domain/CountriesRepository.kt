package com.marcel.pna.headlines.trending.countries.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.flow.Flow

interface CountriesRepository {
    fun getCountries(): Flow<List<Country>>

    suspend fun updateCountries(): Result<CountryError, Unit>
}