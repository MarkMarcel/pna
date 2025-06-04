package com.marcel.pna.headlines.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marcel.pna.headlines.domain.Article
import com.marcel.pna.source.Source

const val ARTICLE_TABLE_NAME = "articles"

@Entity(tableName = ARTICLE_TABLE_NAME)
data class ArticleDatabaseModel(
    @PrimaryKey val id: String,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val sourceId: String,
    val sourceName: String,
    val title: String,
    val url: String?,
    val urlToImage: String?
)

fun ArticleDatabaseModel.toDomain(): Article {
    return Article(
        id = id,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        source = Source(
            id = sourceId,
            name = sourceName
        ),
        title = title,
        url = url,
        urlToImage = urlToImage
    )
}

fun Article.toDatabaseModel(): ArticleDatabaseModel {
    return ArticleDatabaseModel(
        id = id,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        sourceId = source.id,
        sourceName = source.name,
        title = title,
        url = url,
        urlToImage = urlToImage
    )
}
