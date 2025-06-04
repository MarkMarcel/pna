package com.marcel.pna.headlines.domain

import com.marcel.pna.settings.domain.LoadTrendingHeadlinesBy

sealed class HeadlinesRequest(
    open val page: Int,
    open val pageSize: Int,
) {
    data class Category(
        val category: Category,
        override val page: Int,
        override val pageSize: Int,
    ) : HeadlinesRequest(page, pageSize)

    data class Trending(
        val countryAlpha2Code: String?,
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy,
        val headlinesSourcesIds: Set<String>?,
        override val page: Int,
        override val pageSize: Int,
    ) : HeadlinesRequest(page, pageSize) {
        val isValid: Boolean
            get() {
                return when (loadTrendingHeadlinesBy) {
                    LoadTrendingHeadlinesBy.Country -> countryAlpha2Code != null
                    LoadTrendingHeadlinesBy.Sources -> headlinesSourcesIds != null
                }
            }
    }
}