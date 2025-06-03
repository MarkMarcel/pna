package com.marcel.pna.settings.domain

val defaultCountryAlpha2Code = "gh"

enum class LoadTrendingHeadlinesBy {
    Country, Sources,
}

data class Settings(
    val apiKey: String?,
    val countryAlpha2Code: String,
    val headlinesSourcesIds: Set<String>?,
    val headlinesPerRequest: Int,
    val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy,
    val usesDeveloperApiKeys: Boolean,
)
