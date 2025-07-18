package com.marcel.pna.headlines.data

import com.marcel.pna.core.Logger
import com.marcel.pna.headlines.data.models.toDatabaseModel
import com.marcel.pna.headlines.domain.Article

class ArticleLocalDataSource(
    private val articleRoomDao: ArticleRoomDao,
    private val logger: Logger,
) {
    suspend fun saveArticle(article: Article) {
        articleRoomDao.insertArticle(article.toDatabaseModel())
    }
}