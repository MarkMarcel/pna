package com.marcel.pna.headlines

data class HeadlinesConfig(
    val headlinesPerRequest: Int = 20,
    val maxHeadlinesPerRequest: Int = 100,
)
