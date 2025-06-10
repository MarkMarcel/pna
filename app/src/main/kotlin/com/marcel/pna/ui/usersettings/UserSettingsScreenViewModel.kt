package com.marcel.pna.ui.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcel.pna.countries.domain.usecases.CountriesUseCaseProvider
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import com.marcel.pna.usersettings.domain.usecases.UserSettingsUseCaseProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserSettingsScreenViewModel(
    private val countriesUseCaseProvider: CountriesUseCaseProvider,
    private val userSettingsUseCaseProvider: UserSettingsUseCaseProvider,
) : ViewModel() {
    val uiState: StateFlow<UserSettingsScreenUiState>
        get() = modelState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = UserSettingsScreenUiState.NotInitialised
            )

    private val modelState =
        MutableStateFlow<UserSettingsScreenModelState>(UserSettingsScreenModelState.NotInitialised)

    private var userSettingsCollectionJob: Job? = null

    fun onIntent(intent: UserSettingsIntent) {
        when (intent) {
            is UserSettingsIntent.LoadData -> {
                loadData()
            }

            is UserSettingsIntent.SetLoadTrendingHeadlinesBy -> {
                modelState.update { state ->
                    when (state) {
                        is UserSettingsScreenModelState.Initialised -> state.copy(
                            loadTrendingHeadlinesBy = intent.selection
                        )

                        else -> state
                    }
                }
            }

            is UserSettingsIntent.SetTrendingHeadlinesCountry -> {
                updateTrendingHeadlinesCountry(intent.country.alpha2Code)
            }

            is UserSettingsIntent.UpdateCountries -> {
                updateCountries()
            }
        }
    }

    private fun loadData() {
        userSettingsCollectionJob?.cancel()
        userSettingsCollectionJob = viewModelScope.launch {
            combine(
                countriesUseCaseProvider.getCountriesUseCase.run(),
                userSettingsUseCaseProvider.getUserSettingsUseCase.run(),
            ) { countries, userSettings ->
                Pair(countries, userSettings)
            }.collect { (countries, loadedSettings) ->
                modelState.update { state ->
                    when (state) {
                        is UserSettingsScreenModelState.Initialised -> state
                            .copy(countries = countries)
                            .toScreenModelState(loadedSettings = loadedSettings)

                        else -> UserSettingsScreenModelState.Initialised()
                            .copy(countries = countries)
                            .toScreenModelState(loadedSettings = loadedSettings)
                    }
                }
            }
        }
    }

    private fun updateCountries() {
        viewModelScope.launch {
            modelState.update { state ->
                when (state) {
                    is UserSettingsScreenModelState.Initialised -> state.copy(
                        areCountriesUpdating = true
                    )

                    else -> state
                }
            }
            countriesUseCaseProvider.updateCountriesUseCase.run()
                .fold(
                    onFailure = { error -> addError(error.toUserSettingsScreenError()) },
                    onSuccess = {
                        modelState.update { state ->
                            when (state) {
                                is UserSettingsScreenModelState.Initialised -> state.copy(
                                    areCountriesUpdating = false
                                )

                                else -> state
                            }
                        }
                    },
                )
        }
    }

    private fun updateTrendingHeadlinesCountry(alpha2Code: String) {
        viewModelScope.launch {
            val update = UserSettingsUpdate(
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
                    alpha2Code = alpha2Code.lowercase()
                )
            )
            userSettingsUseCaseProvider.updateUserSettingsUseCase.run(userSettingsUpdate = update)
                .fold(
                    onFailure = { error -> addError(error.toUserSettingsScreenError()) },
                    onSuccess = {},
                )
        }
    }

    private fun addError(error: UserSettingsScreenError) {
        modelState.update { state ->
            when (state) {
                is UserSettingsScreenModelState.Initialised -> state.copy(
                    errors = state.errors + error
                )

                else -> UserSettingsScreenModelState.Initialised(
                    countries = emptyList(),
                    country = null,
                    areCountriesUpdating = false,
                    errors = listOf(error),
                    loadTrendingHeadlinesBy = LoadTrendingHeadlinesBySelection.Country,
                    sourcesIds = null
                )
            }
        }
    }
}