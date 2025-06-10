package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.countries.domain.CountriesRepository
import kotlinx.coroutines.CoroutineDispatcher

class CountriesUseCaseProvider(
    backgroundDispatcher: CoroutineDispatcher,
    countriesRepository: CountriesRepository
) {
    val getCountriesUseCase = GetCountriesUseCase(
        backgroundDispatcher = backgroundDispatcher,
        countriesRepository = countriesRepository,
    )

    val updateCountriesUseCase = UpdateCountriesUseCase(
        backgroundDispatcher = backgroundDispatcher,
        countriesRepository = countriesRepository
    )
}