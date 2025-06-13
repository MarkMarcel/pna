package com.marcel.pna.headlines.domain

import com.marcel.pna.headlines.category.ArticleCategory
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy

sealed class HeadlinesRequest(
    open val page: Int,
    open val pageSize: Int?,
) {
    data class Category(
        val category: ArticleCategory,
        override val page: Int = 1,
        override val pageSize: Int? = null,
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

    companion object {
        val HeadlinesRequest.nextPage: HeadlinesRequest
            get() = when (this) {
                is Category -> copy(page = page + 1)
                is Trending -> copy(page = page + 1)
            }
    }
}