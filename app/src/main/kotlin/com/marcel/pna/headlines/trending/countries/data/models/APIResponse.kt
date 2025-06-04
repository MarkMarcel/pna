package com.marcel.pna.headlines.trending.countries.data.models

import com.marcel.pna.headlines.trending.countries.domain.Country
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestCountryApiResponse(
    val name: CountryName,
    val cca2: String,
    val translations: Map<String, Translation>
)

@JsonClass(generateAdapter = true)
data class CountryName(
    val common: String,
    val official: String,
    val nativeName: Map<String, Translation>
)

@JsonClass(generateAdapter = true)
data class Translation(
    val official: String,
    val common: String
)

fun RestCountryApiResponse.toDomain(): Country {
    val englishName = this.name.common
    val germanName = this.translations["deu"]?.common ?: this.name.common
    return Country(
        alpha2Code = this.cca2,
        englishName = englishName,
        germanName = germanName
    )
}
