package com.marcel.pna.headlines.trending.countries.domain.usecases

import com.marcel.pna.core.Result
import com.marcel.pna.headlines.trending.countries.domain.CountriesRepository
import com.marcel.pna.headlines.trending.countries.domain.CountryError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateCountriesUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val countriesRepository: CountriesRepository,
) {
    suspend fun invoke(): Result<CountryError, Unit> {
        return withContext(backgroundDispatcher) {
            countriesRepository.updateCountries()
        }
    }
}