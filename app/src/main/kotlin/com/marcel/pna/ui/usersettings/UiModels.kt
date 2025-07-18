package com.marcel.pna.ui.usersettings

import com.marcel.pna.countries.domain.Country
import com.marcel.pna.countries.domain.CountryError
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError
import java.util.Locale

data class UiCountry(val alpha2Code: String, val name: String)

enum class LoadTrendingHeadlinesBySelection {
    Country, Sources
}

enum class UserSettingsScreenError {
    FailedToSave, NetWork, NoCountry, NoSources, Server, Unknown
}

sealed class UserSettingsScreenIntent {
    data object ErrorHandled : UserSettingsScreenIntent()

    data class LoadData(val languageCode: String) : UserSettingsScreenIntent()

    data class SetLanguageCode(val languageCode: String) : UserSettingsScreenIntent()

    data class SetLoadTrendingHeadlinesBy(
        val selection: LoadTrendingHeadlinesBySelection
    ) : UserSettingsScreenIntent()

    data class SetNewsApiKey(val apiKey: String) : UserSettingsScreenIntent()

    data class SetTrendingHeadlinesCountry(val country: UiCountry) : UserSettingsScreenIntent()

    data class UpdateNewsApiKey(val updatedApiKey: String) : UserSettingsScreenIntent()

    data object UpdateCountries : UserSettingsScreenIntent()
}

sealed class UserSettingsScreenUiState {
    data object NotInitialised : UserSettingsScreenUiState()

    data class Initialised(
        val areCountriesUpdating: Boolean,
        val countries: List<UiCountry>,
        val country: UiCountry?,
        val error: UserSettingsScreenError?,
        val generateNewsApiUrl: String,
        val isSettingNewsApiKey: Boolean,
        val languageCode: String?,
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
        val newsApiKey: String,
        val sourcesIds: Set<String>?,
    ) : UserSettingsScreenUiState()
}

sealed class UserSettingsScreenModelState {
    data object NotInitialised : UserSettingsScreenModelState()

    data class Initialised(
        val areCountriesUpdating: Boolean = false,
        val countries: List<Country> = emptyList(),
        val country: Country? = null,
        val errors: List<UserSettingsScreenError> = emptyList(),
        val generateNewsApiUrl: String,
        val isSettingNewsApiKey: Boolean = false,
        val languageCode: String = "",
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection = LoadTrendingHeadlinesBySelection.Country,
        val newsApiKey: String = "",
        val sourcesIds: Set<String>? = null,
    ) : UserSettingsScreenModelState()

    companion object {
        fun UserSettingsScreenModelState.asInitialised(
            generateNewsApiUrl: String
        ): Initialised = when (this) {
            is Initialised -> this
            is NotInitialised -> {
                Initialised(
                    generateNewsApiUrl = generateNewsApiUrl
                )
            }
        }
    }
}

fun CountryError.toUserSettingsScreenError() = when (this) {
    CountryError.Network -> UserSettingsScreenError.NetWork
    CountryError.Server -> UserSettingsScreenError.Server
}

fun UserSettingsError.toUserSettingsScreenError() = when (this) {
    UserSettingsError.FailedToSave -> UserSettingsScreenError.FailedToSave
    UserSettingsError.NoCountry -> UserSettingsScreenError.NoCountry
    UserSettingsError.NoSources -> UserSettingsScreenError.NoSources
}

fun UserSettingsScreenModelState.toUiState() = when (this) {
    is UserSettingsScreenModelState.NotInitialised -> UserSettingsScreenUiState.NotInitialised

    is UserSettingsScreenModelState.Initialised -> UserSettingsScreenUiState.Initialised(
        newsApiKey = newsApiKey,
        areCountriesUpdating = areCountriesUpdating,
        countries = countries.map { it.toUi(languageCode) }.sortedBy { it.name },
        country = country?.toUi(languageCode),
        error = errors.firstOrNull(),
        generateNewsApiUrl = generateNewsApiUrl,
        isSettingNewsApiKey = isSettingNewsApiKey,
        languageCode = languageCode,
        loadTrendingHeadlinesBy = loadTrendingHeadlinesBy,
        sourcesIds = sourcesIds
    )
}

fun UserSettingsScreenModelState.Initialised.toScreenModelState(
    loadedSettings: UserSettings
): UserSettingsScreenModelState {
    var loadTrendingHeadlinesBySelection = LoadTrendingHeadlinesBySelection.Country
    var countryAlpha2Code: String? = null
    var sourcesIds: Set<String>? = null
    when (val loadTrendingHeadlinesBy = loadedSettings.loadTrendingHeadlinesBy) {
        is LoadTrendingHeadlinesBy.Country -> {
            countryAlpha2Code = loadTrendingHeadlinesBy.alpha2Code
        }

        is LoadTrendingHeadlinesBy.Sources -> {
            loadTrendingHeadlinesBySelection = LoadTrendingHeadlinesBySelection.Sources
            sourcesIds = loadTrendingHeadlinesBy.sourceIds
        }
    }
    return copy(
        newsApiKey = loadedSettings.newsApiKey,
        loadTrendingHeadlinesBy = loadTrendingHeadlinesBySelection,
        country = countries.find { it.alpha2Code.lowercase() == countryAlpha2Code?.lowercase() },
        sourcesIds = sourcesIds
    )
}

private fun Country.toUi(languageCode: String) = UiCountry(
    alpha2Code = alpha2Code,
    name = when (languageCode) {
        Locale.GERMAN.language -> germanName
        else -> englishName
    }
)
