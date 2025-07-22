package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.Country
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetCountriesUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val countriesRepository: CountriesRepository,
) {
    fun run(): Flow<List<Country>> {
        return countriesRepository.getCountries()
            .flowOn(backgroundDispatcher)
    }
}