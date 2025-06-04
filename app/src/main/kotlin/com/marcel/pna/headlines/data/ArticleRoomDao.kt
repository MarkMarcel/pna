package com.marcel.pna.headlines.data

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.marcel.pna.headlines.data.models.ArticleDatabaseModel

interface ArticleRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleDatabaseModel)
}