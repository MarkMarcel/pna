package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.core.Result
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.CountryError
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