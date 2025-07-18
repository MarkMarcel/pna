package com.marcel.pna.headlines.data

import com.marcel.pna.headlines.data.models.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HeadlinesApi {
    /**
     * @param country The 2-letter ISO 3166-1 code of the country you want to get headlines for (e.g., "us", "gb").
     */
    @GET("top-headlines")
    suspend fun getTrendingHeadlinesFromCountry(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsApiResponse
}