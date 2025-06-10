package com.marcel.pna.ui.usersettings

import com.marcel.pna.countries.domain.Country
import com.marcel.pna.countries.domain.CountryError
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError

enum class LoadTrendingHeadlinesBySelection {
    Country, Sources
}

enum class UserSettingsScreenError {
    FailedToSave, NetWork, NoCountry, NoSources, Server, Unknown
}

sealed class UserSettingsIntent {
    data object LoadData : UserSettingsIntent()

    data class SetLoadTrendingHeadlinesBy(
        val selection: LoadTrendingHeadlinesBySelection
    ) : UserSettingsIntent()

    data class SetTrendingHeadlinesCountry(val country: Country) : UserSettingsIntent()

    data object UpdateCountries : UserSettingsIntent()
}

sealed class UserSettingsScreenUiState {
    data object NotInitialised : UserSettingsScreenUiState()
    data class Initialised(
        val areCountriesUpdating: Boolean,
        val countries: List<Country>,
        val country: Country?,
        val error: UserSettingsScreenError?,
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
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
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection = LoadTrendingHeadlinesBySelection.Country,
        val sourcesIds: Set<String>? = null,
    ) : UserSettingsScreenModelState()
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
        areCountriesUpdating = areCountriesUpdating,
        countries = countries,
        country = country,
        error = errors.firstOrNull(),
        loadTrendingHeadlinesBy = loadTrendingHeadlinesBy,
        sourcesIds = sourcesIds
    )
}

fun UserSettingsScreenModelState.Initialised.toScreenModelState(loadedSettings: UserSettings): UserSettingsScreenModelState {
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
        loadTrendingHeadlinesBy = loadTrendingHeadlinesBySelection,
        country = countries.find { it.alpha2Code.lowercase() == countryAlpha2Code?.lowercase() },
        sourcesIds = sourcesIds
    )
}
