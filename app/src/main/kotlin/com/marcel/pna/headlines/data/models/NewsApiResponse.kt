package com.marcel.pna.headlines.data.models

import com.marcel.pna.headlines.domain.Article
import com.marcel.pna.headlines.domain.HeadlinesPage
import com.marcel.pna.source.Source
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleApiResponse>
)

@JsonClass(generateAdapter = true)
data class NewsErrorApiResponse(
    val status: String,
    val code: String,
    val message: String
)

@JsonClass(generateAdapter = true)
data class ArticleApiResponse(
    val source: SourceApiResponse?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

@JsonClass(generateAdapter = true)
data class SourceApiResponse(
    val id: String?,
    val name: String?
)

fun NewsApiResponse.toHeadlinesPage(
    currentPage: Int,
    pageSize: Int,
    getLocalIdForArticle: () -> String
): HeadlinesPage {
    val articles: List<Article> = this.articles.mapNotNull {
        it.toDomain(getLocalId = getLocalIdForArticle)
    }

    val totalPages = if (totalResults > 0 && pageSize > 0)
        (totalResults + pageSize - 1) / pageSize
    else
        null

    val nextPage = if (totalPages != null && currentPage < totalPages) currentPage + 1 else null

    return HeadlinesPage(
        headlines = articles,
        nextPage = nextPage
    )
}


fun ArticleApiResponse.toDomain(
    getLocalId: () -> String
): Article? {
    // Ensure all required non-nullable properties are present
    if (
        author == null ||
        content == null ||
        description == null ||
        publishedAt == null ||
        title == null ||
        source?.id == null ||
        source.name == null
    ) {
        return null
    }

    return Article(
        id = getLocalId(),
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        title = title,
        url = url,
        urlToImage = urlToImage,
        source = Source(
            id = source.id,
            name = source.name
        )
    )
}

fun getLocalIdForArticle(): String = UUID.randomUUID().toString()

