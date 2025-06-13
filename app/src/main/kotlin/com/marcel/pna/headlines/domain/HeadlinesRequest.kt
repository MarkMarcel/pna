package com.marcel.pna.headlines.domain

import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy

sealed class HeadlinesRequest(
    open val page: Int,
    open val pageSize: Int?,
) {
    data class Category(
        val category: Category,
        override val page: Int,
        override val pageSize: Int,
    ) : HeadlinesRequest(page, pageSize)

    data class Trending(
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy,
        override val page: Int = 1,
        override val pageSize: Int? = null,
    ) : HeadlinesRequest(page, pageSize) {
        val isValid: Boolean
            get() {
                return when (loadTrendingHeadlinesBy) {
                    is LoadTrendingHeadlinesBy.Country -> loadTrendingHeadlinesBy.alpha2Code.isNotBlank()
                    is LoadTrendingHeadlinesBy.Sources -> loadTrendingHeadlinesBy.sourceIds.isNotEmpty()
                }
            }
    }
}