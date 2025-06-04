package com.marcel.pna.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcel.pna.headlines.data.ArticleRoomDao
import com.marcel.pna.headlines.data.models.ArticleDatabaseModel
import com.marcel.pna.headlines.trending.countries.data.CountriesRoomDao
import com.marcel.pna.headlines.trending.countries.data.models.CountryDatabaseModel

@Database(
    version = 1,
    entities = [
        ArticleDatabaseModel::class,
        CountryDatabaseModel::class,
    ],
)
abstract class PNAMDatabase : RoomDatabase() {
    abstract fun articleRoomDao(): ArticleRoomDao
    abstract fun countriesRoomDao(): CountriesRoomDao
}