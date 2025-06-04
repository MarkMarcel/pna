package com.marcel.pna.headlines.trending.countries.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marcel.pna.headlines.trending.countries.domain.Country

const val COUNTRY_TABLE_NAME = "countries"

@Entity(tableName = COUNTRY_TABLE_NAME)
data class CountryDatabaseModel(
    @PrimaryKey val alpha2Code: String,
    val englishName: String,
    val germanName: String,
)

fun CountryDatabaseModel.toDomain() = Country(
    alpha2Code = alpha2Code,
    englishName = englishName,
    germanName = germanName,
)

fun Country.toDatabaseModel() = CountryDatabaseModel(
    alpha2Code = alpha2Code,
    englishName = englishName,
    germanName = germanName,
)
