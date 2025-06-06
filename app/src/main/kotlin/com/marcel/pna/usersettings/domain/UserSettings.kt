package com.marcel.pna.usersettings.domain

const val defaultCountryAlpha2Code = "gh"

data class UserSettings(
    val apiKey: String,
    val headlinesPerRequest: Int,
    val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy,
    val usesDeveloperApiKeys: Boolean,
)

sealed class LoadTrendingHeadlinesBy {
    data class Country(val alpha2Code: String) : LoadTrendingHeadlinesBy()
    data class Sources(val sourceIds: Set<String>): LoadTrendingHeadlinesBy()
}
