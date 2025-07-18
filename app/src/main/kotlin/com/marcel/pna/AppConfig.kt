package com.marcel.pna

import com.marcel.pna.headlines.domain.HeadlinesConfig

data class AppConfig(
    val headlinesConfig: HeadlinesConfig = HeadlinesConfig(),
    val servicesConfig: ServicesConfig = ServicesConfig(
        newsApiKeyGenerationUrl = "https://newsapi.org/register"
    )
)

data class ServicesConfig(val newsApiKeyGenerationUrl: String)
