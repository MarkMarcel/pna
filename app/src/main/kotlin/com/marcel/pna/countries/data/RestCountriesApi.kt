package com.marcel.pna.countries.data

import com.marcel.pna.countries.data.models.RestCountryApiResponse
import retrofit2.http.GET

interface RestCountriesApi {
    @GET("all")
    suspend fun getCountries(): List<RestCountryApiResponse>
}