package com.marcel.pna.settings.domain

data class SettingsUpdate(
    val apiKey: String? = null,
    val countryAlpha2Code: String? = null,
    val headlinesSourcesIds: Set<String>? = null,
    val headlinesPerRequest: Int? = null,
    val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy? = null,
    val usesDeveloperApiKeys: Boolean? = null,
)
