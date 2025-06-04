package com.marcel.pna.headlines.trending.countries

import com.marcel.pna.headlines.trending.countries.data.models.RestCountryApiResponse
import com.marcel.pna.headlines.trending.countries.data.models.CountryName
import com.marcel.pna.headlines.trending.countries.data.models.Translation
import com.marcel.pna.headlines.trending.countries.domain.Country
import com.marcel.pna.headlines.trending.countries.data.models.CountryDatabaseModel

val countriesTestData = listOf(
    Country(alpha2Code = "MN", englishName = "Mongolia", germanName = "Mongolei"),
    Country(alpha2Code = "PA", englishName = "Panama", germanName = "Panama"),
    Country(alpha2Code = "TO", englishName = "Tonga", germanName = "Tonga"),
    Country(alpha2Code = "KM", englishName = "Comoros", germanName = "Komoren"),
    Country(alpha2Code = "SE", englishName = "Sweden", germanName = "Schweden")
)

val countryApiResponsesTestData = listOf(
    RestCountryApiResponse(
        name = CountryName(
            common = "Mongolia",
            official = "Mongolia",
            nativeName = mapOf(
                "mon" to Translation(official = "Монгол улс", common = "Монгол улс")
            )
        ),
        cca2 = "MN",
        translations = mapOf(
            "deu" to Translation(official = "Mongolei", common = "Mongolei"),
            "fra" to Translation(official = "Mongolie", common = "Mongolie"),
            "rus" to Translation(official = "Монголия", common = "Монголия")
        )
    ),
    RestCountryApiResponse(
        name = CountryName(
            common = "Panama",
            official = "Republic of Panama",
            nativeName = mapOf(
                "spa" to Translation(official = "República de Panamá", common = "Panamá")
            )
        ),
        cca2 = "PA",
        translations = mapOf(
            "deu" to Translation(official = "Republik Panama", common = "Panama"),
            "fra" to Translation(official = "République du Panama", common = "Panama")
        )
    ),
    RestCountryApiResponse(
        name = CountryName(
            common = "Tonga",
            official = "Kingdom of Tonga",
            nativeName = mapOf(
                "eng" to Translation(official = "Kingdom of Tonga", common = "Tonga"),
                "ton" to Translation(official = "Kingdom of Tonga", common = "Tonga")
            )
        ),
        cca2 = "TO",
        translations = mapOf(
            "deu" to Translation(official = "Königreich Tonga", common = "Tonga"),
            "fra" to Translation(official = "Royaume des Tonga", common = "Tonga")
        )
    ),
    RestCountryApiResponse(
        name = CountryName(
            common = "Comoros",
            official = "Union of the Comoros",
            nativeName = mapOf(
                "ara" to Translation(official = "الاتحاد القمري", common = "القمر‎"),
                "fra" to Translation(official = "Union des Comores", common = "Comores")
            )
        ),
        cca2 = "KM",
        translations = mapOf(
            "deu" to Translation(official = "Union der Komoren", common = "Komoren"),
            "fra" to Translation(official = "Union des Comores", common = "Comores")
        )
    ),
    RestCountryApiResponse(
        name = CountryName(
            common = "Sweden",
            official = "Kingdom of Sweden",
            nativeName = mapOf(
                "swe" to Translation(official = "Konungariket Sverige", common = "Sverige")
            )
        ),
        cca2 = "SE",
        translations = mapOf(
            "deu" to Translation(official = "Königreich Schweden", common = "Schweden"),
            "fra" to Translation(official = "Royaume de Suède", common = "Suède")
        )
    )
)

val countryDatabaseModelsTestData = listOf(
    CountryDatabaseModel(
        alpha2Code = "MN",
        englishName = "Mongolia",
        germanName = "Mongolei"
    ),
    CountryDatabaseModel(
        alpha2Code = "PA",
        englishName = "Panama",
        germanName = "Panama"
    ),
    CountryDatabaseModel(
        alpha2Code = "TO",
        englishName = "Tonga",
        germanName = "Tonga"
    ),
    CountryDatabaseModel(
        alpha2Code = "KM",
        englishName = "Comoros",
        germanName = "Komoren"
    ),
    CountryDatabaseModel(
        alpha2Code = "SE",
        englishName = "Sweden",
        germanName = "Schweden"
    )
)

