package com.marcel.pna.headlines.domain

import com.marcel.pna.source.Source

/**
 * @property publishedAt The UTC date and time the article was published, in ISO 8601 format (e.g., "YYYY-MM-DDTHH:mm:ssZ").
 */
data class Article(
    val id: String,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String?,
    val urlToImage: String?,
)


