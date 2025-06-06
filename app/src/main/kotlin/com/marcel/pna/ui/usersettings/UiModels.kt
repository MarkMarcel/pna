package com.marcel.pna.ui.usersettings

import com.marcel.pna.countries.domain.Country
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError
import com.marcel.pna.usersettings.domain.UserSettingsUpdate

enum class LoadTrendingHeadlinesBySelection {
    Country, Sources
}

sealed class UserSettingsIntent {
    data object LoadSettings : UserSettingsIntent()
    data class SetLoadTrendingHeadlinesBy(
        val selection: LoadTrendingHeadlinesBySelection
    ) : UserSettingsIntent()

    data class SetTrendingHeadlinesCountry(val country: Country) : UserSettingsIntent()
}

sealed class UserSettingsScreenUiState {
    data object NotInitialised : UserSettingsScreenUiState()
    data class Initialised(
        val areCountriesUpdating: Boolean,
        val countries: List<Country>,
        val country: Country?,
        val error: UserSettingsError?,
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
        val sourcesIds: Set<String>?,
    ) : UserSettingsScreenUiState()
}

sealed class UserSettingsScreenModelState {
    data object NotInitialised : UserSettingsScreenModelState()
    data class Initialised(
        val areCountriesUpdating: Boolean = false,
        val countries: List<Country>,
        val country: Country?,
        val errors: List<UserSettingsError> = emptyList(),
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
        val sourcesIds: Set<String>?,
    ) : UserSettingsScreenModelState()
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

fun UserSettingsScreenModelState.Initialised.toUserSettingsUpdate(): UserSettingsUpdate {
    val loadTrendingHeadlinesBy = when (loadTrendingHeadlinesBy) {
        LoadTrendingHeadlinesBySelection.Country -> LoadTrendingHeadlinesBy.Country(
            alpha2Code = country?.alpha2Code ?: ""
        )

        LoadTrendingHeadlinesBySelection.Sources -> LoadTrendingHeadlinesBy.Sources(
            sourceIds = sourcesIds ?: emptySet()
        )
    }
    return UserSettingsUpdate()
}

private fun UserSettingsScreenModelState.Initialised.toScreenModelState(loadedSettings: UserSettings): UserSettingsScreenModelState {
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
