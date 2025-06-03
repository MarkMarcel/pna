package com.marcel.pna.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcel.pna.headlines.trending.countries.data.CountriesRoomDao
import com.marcel.pna.headlines.trending.countries.domain.CountryDatabaseModel

@Database(entities = [CountryDatabaseModel::class], version = 1)
abstract class PNAMDatabase : RoomDatabase() {
    abstract fun countriesRoomDao(): CountriesRoomDao
}