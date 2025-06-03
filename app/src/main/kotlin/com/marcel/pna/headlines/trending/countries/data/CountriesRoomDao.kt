package com.marcel.pna.headlines.trending.countries.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marcel.pna.headlines.trending.countries.domain.CountryDatabaseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesRoomDao {
    @Query("SELECT * FROM countries")
    fun getCountries(): Flow<List<CountryDatabaseModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryDatabaseModel>)
}