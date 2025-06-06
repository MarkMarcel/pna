package com.marcel.pna.usersettings.domain

data class UserSettingsUpdate(
    val apiKey: String? = null,
    val headlinesPerRequest: Int? = null,
    val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy? = null,
    val usesDeveloperApiKeys: Boolean? = null,
) {
    fun getError(): UserSettingsError? {
        when (loadTrendingHeadlinesBy) {
            is LoadTrendingHeadlinesBy.Country -> {
                if (loadTrendingHeadlinesBy.alpha2Code.isBlank())
                    return UserSettingsError.NoCountry
            }

            is LoadTrendingHeadlinesBy.Sources -> {
                if (loadTrendingHeadlinesBy.sourceIds.isEmpty()) {
                    return UserSettingsError.NoSources
                }
            }

            else -> {}
        }
        return null
    }
}
