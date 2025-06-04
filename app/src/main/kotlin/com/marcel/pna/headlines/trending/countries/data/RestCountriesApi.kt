package com.marcel.pna.headlines.trending.countries.data

import com.marcel.pna.headlines.trending.countries.data.models.RestCountryApiResponse
import retrofit2.http.GET

interface RestCountriesApi {
    @GET("all")
    suspend fun getCountries(): List<RestCountryApiResponse>
}