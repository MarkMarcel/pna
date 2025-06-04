package com.marcel.pna

import com.marcel.pna.headlines.domain.HeadlinesConfig

data class AppConfig(
    val headlinesConfig: HeadlinesConfig = HeadlinesConfig()
)
