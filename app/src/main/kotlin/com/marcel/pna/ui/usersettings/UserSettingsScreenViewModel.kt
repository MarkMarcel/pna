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
            is UserSettingsIntent.ErrorHandled -> {
                onErrorHandled()
            }

            is UserSettingsIntent.LoadData -> {
                loadData(languageCode = intent.languageCode)
            }

            is UserSettingsIntent.SetLanguageCode -> {
                modelState.update { state ->
                    when (state) {
                        is UserSettingsScreenModelState.Initialised -> state.copy(
                            languageCode = intent.languageCode
                        )

                        else -> state
                    }
                }
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

            is UserSettingsIntent.SetNewsApiKey -> {
                updateNewsApiKey(apiKey = intent.apiKey)
            }

            is UserSettingsIntent.SetTrendingHeadlinesCountry -> {
                updateTrendingHeadlinesCountry(intent.country.alpha2Code)
            }

            is UserSettingsIntent.UpdateCountries -> {
                updateCountries()
            }

            is UserSettingsIntent.UpdateNewsApiKey -> {
                modelState.update { state ->
                    when (state) {
                        is UserSettingsScreenModelState.Initialised -> state.copy(
                            newsApiKey = intent.updatedApiKey
                        )

                        else -> state
                    }
                }
            }
        }
    }

    private fun loadData(languageCode: String) {
        userSettingsCollectionJob?.cancel()
        userSettingsCollectionJob = viewModelScope.launch {
            combine(
                countriesUseCaseProvider.getCountriesUseCase.run(),
                userSettingsUseCaseProvider.getUserSettingsUseCase.run(),
            ) { countries, userSettings ->
                Pair(countries, userSettings)
            }.collect { (countries, loadedSettings) ->
                modelState.update { state ->
                    val stateToUpdate = when (state) {
                        is UserSettingsScreenModelState.Initialised -> state

                        else -> UserSettingsScreenModelState.Initialised()
                    }
                    stateToUpdate.copy(countries = countries, languageCode = languageCode)
                        .toScreenModelState(loadedSettings = loadedSettings)
                }
            }
        }
    }

    private fun onErrorHandled() {
        modelState.update { state ->
            when (state) {
                is UserSettingsScreenModelState.Initialised -> state.copy(
                    errors = state.errors.dropLast(1)
                )

                else -> state
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
                    onFailure = { error ->
                        modelState.update { state ->
                            when (state) {
                                is UserSettingsScreenModelState.Initialised -> state.copy(
                                    areCountriesUpdating = false
                                )

                                else -> state
                            }
                        }
                        addError(error.toUserSettingsScreenError())
                    },
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

    private fun updateNewsApiKey(apiKey: String) {
        viewModelScope.launch {
            modelState.update { state ->
                when (state) {
                    is UserSettingsScreenModelState.Initialised -> state.copy(
                        isSettingNewsApiKey = true
                    )

                    else -> state
                }
            }
            val update = UserSettingsUpdate(
                newsApiKey = apiKey
            )
            userSettingsUseCaseProvider.updateUserSettingsUseCase.run(userSettingsUpdate = update)
                .fold(
                    onFailure = { error ->
                        modelState.update { state ->
                            when (state) {
                                is UserSettingsScreenModelState.Initialised -> state.copy(
                                    isSettingNewsApiKey = false
                                )

                                else -> state
                            }
                        }
                        addError(error.toUserSettingsScreenError())
                    },
                    onSuccess = {
                        modelState.update { state ->
                            when (state) {
                                is UserSettingsScreenModelState.Initialised -> state.copy(
                                    isSettingNewsApiKey = false
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

                else -> UserSettingsScreenModelState.Initialised().copy(
                    errors = listOf(error)
                )
            }
        }
    }
}