package com.marcel.pna.headlines.domain

data class HeadlinesPage(
    val headlines: List<Article>,
    val nextPage: Int?
)
