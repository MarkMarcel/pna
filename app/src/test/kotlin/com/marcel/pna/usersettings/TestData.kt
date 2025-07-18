package com.marcel.pna.usersettings

import com.marcel.pna.countries.countriesTestData
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings

val userSettingsDefaultTestInstance = UserSettings(
    loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
        alpha2Code = countriesTestData.first().alpha2Code
    ),
    newsApiKey = "apiKey",
    headlinesPerRequest = 10,
    usesDeveloperNewsApiKeys = true
)