package com.marcel.pna.ui.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcel.pna.AppConfig
import com.marcel.pna.countries.domain.usecases.CountriesUseCaseProvider
import com.marcel.pna.ui.usersettings.UserSettingsScreenModelState.Companion.asInitialised
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
    private val appConfigProvider: () -> AppConfig,
    private val countriesUseCaseProvider: CountriesUseCaseProvider,
    private val userSettingsUseCaseProvider: UserSettingsUseCaseProvider,
) : ViewModel() {
    val uiState: StateFlow<UserSettingsScreenUiState> by lazy {
        modelState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = UserSettingsScreenModelState.NotInitialised.toUiState()
            )
    }

    private val modelState =
        MutableStateFlow<UserSettingsScreenModelState>(UserSettingsScreenModelState.NotInitialised)

    private var userSettingsCollectionJob: Job? = null

    fun onIntent(intent: UserSettingsScreenIntent) {
        when (intent) {
            is UserSettingsScreenIntent.ErrorHandled -> {
                onErrorHandled()
            }

            is UserSettingsScreenIntent.LoadData -> {
                loadData(languageCode = intent.languageCode)
            }

            is UserSettingsScreenIntent.SetLanguageCode -> {
                modelState.update { state ->
                    state.asInitialised(
                        generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                    ).run {
                        copy(languageCode = intent.languageCode)
                    }
                }
            }

            is UserSettingsScreenIntent.SetLoadTrendingHeadlinesBy -> {
                modelState.update { state ->
                    state.asInitialised(
                        generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                    ).run {
                        copy(loadTrendingHeadlinesBy = intent.selection)
                    }
                }
            }

            is UserSettingsScreenIntent.SetNewsApiKey -> {
                updateNewsApiKey(apiKey = intent.apiKey)
            }

            is UserSettingsScreenIntent.SetTrendingHeadlinesCountry -> {
                updateTrendingHeadlinesCountry(intent.country.alpha2Code)
            }

            is UserSettingsScreenIntent.UpdateCountries -> {
                updateCountries()
            }

            is UserSettingsScreenIntent.UpdateNewsApiKey -> {
                modelState.update { state ->
                    state.asInitialised(
                        generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                    ).run {
                        copy(newsApiKey = intent.updatedApiKey)
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
                    state.asInitialised(
                        generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                    ).run {
                        copy(
                            countries = countries,
                            languageCode = languageCode
                        ).toScreenModelState(
                            loadedSettings = loadedSettings
                        )
                    }
                }
            }
        }
    }

    private fun onErrorHandled() {
        viewModelScope.launch {
            modelState.update { state ->
                state.asInitialised(
                    generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                ).run {
                    copy(
                        errors = errors.dropLast(1)
                    )
                }
            }
        }
    }

    private fun updateCountries() {
        viewModelScope.launch {
            modelState.update { state ->
                state.asInitialised(
                    generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                ).run {
                    copy(areCountriesUpdating = true)
                }
            }
            countriesUseCaseProvider.updateCountriesUseCase.run()
                .fold(
                    onFailure = { error ->
                        modelState.update { state ->
                            state.asInitialised(
                                generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                            ).run {
                                copy(areCountriesUpdating = false)
                            }
                        }
                        addError(error.toUserSettingsScreenError())
                    },
                    onSuccess = {
                        modelState.update { state ->
                            state.asInitialised(
                                generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                            ).run {
                                copy(areCountriesUpdating = false)
                            }
                        }
                    },
                )
        }
    }

    private fun updateNewsApiKey(apiKey: String) {
        viewModelScope.launch {
            modelState.update { state ->
                state.asInitialised(
                    generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                ).run {
                    copy(isSettingNewsApiKey = true)
                }
            }
            val update = UserSettingsUpdate(
                newsApiKey = apiKey
            )
            userSettingsUseCaseProvider.updateUserSettingsUseCase.run(userSettingsUpdate = update)
                .fold(
                    onFailure = { error ->
                        modelState.update { state ->
                            state.asInitialised(
                                generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                            ).run {
                                copy(isSettingNewsApiKey = false)
                            }
                        }
                        addError(error.toUserSettingsScreenError())
                    },
                    onSuccess = {
                        modelState.update { state ->
                            state.asInitialised(
                                generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
                            ).run {
                                copy(isSettingNewsApiKey = false)
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
            state.asInitialised(
                generateNewsApiUrl = appConfigProvider().servicesConfig.newsApiKeyGenerationUrl,
            ).run {
                copy(errors = errors + error)
            }
        }
    }
}