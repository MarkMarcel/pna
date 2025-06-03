package com.marcel.pna.headlines.trending.countries.domain.usecases

import com.marcel.pna.headlines.trending.countries.domain.CountriesRepository
import com.marcel.pna.headlines.trending.countries.domain.Country
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetCountriesUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val countriesRepository: CountriesRepository,
) {
    operator fun invoke(): Flow<List<Country>> {
        return countriesRepository.getCountries()
            .flowOn(backgroundDispatcher)
    }
}