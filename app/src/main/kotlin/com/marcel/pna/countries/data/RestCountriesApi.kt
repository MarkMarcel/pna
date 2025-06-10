package com.marcel.pna.countries.data

import com.marcel.pna.countries.data.models.RestCountryApiResponse
import retrofit2.http.GET

interface RestCountriesApi {
    @GET("all?fields=cca2,translations,name")
    suspend fun getCountries(): List<RestCountryApiResponse>
}