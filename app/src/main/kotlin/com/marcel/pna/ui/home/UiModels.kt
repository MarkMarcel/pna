package com.marcel.pna.ui.home

import com.marcel.pna.headlines.category.ArticleCategory
import com.marcel.pna.headlines.domain.HeadlinesLoadError
import com.marcel.pna.headlines.domain.HeadlinesPage
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.defaultCountryAlpha2Code

sealed class HomeScreenContent {
    data class Headlines(val pagedHeadlines: HeadlinesPage) : HomeScreenContent()

    data class Message(val message: HomeScreenMessage) : HomeScreenContent()
}

sealed class HomeScreenIntent {
    data object ErrorHandled : HomeScreenIntent()

    data object FirstPageHeadlinesLoad : HomeScreenIntent()

    data object Initialisation : HomeScreenIntent()

    data object NextPageHeadlinesLoad : HomeScreenIntent()

    data class SelectedHeadlineCategoryChanged(val index: Int) : HomeScreenIntent()
}

enum class HomeScreenError {
    NETWORK,
    NEWS_API_KEY,
    RATE_LIMITED,
    SOURCE_DOES_NOT_EXIST,
    SERVER
}

enum class HomeScreenMessage {
    INVALID_NEWS_API_KEY
}

sealed class HomeScreenModelState {
    data object NotInitialised : HomeScreenModelState()

    data class Initialised(
        val errors: List<HomeScreenError> = emptyList(),
        val headlineCategories: List<ArticleCategory> = ArticleCategory.entries,
        val isLoadingHeadlines: Boolean = false,
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
            alpha2Code = defaultCountryAlpha2Code
        ),
        val pagedHeadlines: HeadlinesPage = HeadlinesPage(headlines = emptyList(), nextPage = null),
        val selectedCategoryIndex: Int = 0,
    ) : HomeScreenModelState()

    companion object {
        val HomeScreenModelState.initialised: Initialised
            get() = when (this) {
                is Initialised -> this
                is NotInitialised -> {
                    Initialised()
                }
            }
    }
}

sealed class HomeScreenUiState {
    data object NotInitialised : HomeScreenUiState()

    data class Initialised(
        val content: HomeScreenContent,
        val error: HomeScreenError?,
        val headlineCategories: List<ArticleCategory>,
        val isLoadingHeadlines: Boolean,
        val selectedCategoryIndex: Int,
    ) : HomeScreenUiState()
}

fun HomeScreenModelState.toUiState(): HomeScreenUiState = when (this) {
    is HomeScreenModelState.NotInitialised -> HomeScreenUiState.NotInitialised
    is HomeScreenModelState.Initialised -> {
        val error = errors.firstOrNull()
        val message = error?.toMessage()
        val content = if (message != null) {
            HomeScreenContent.Message(message = message)
        } else {
            HomeScreenContent.Headlines(pagedHeadlines = pagedHeadlines)
        }
        HomeScreenUiState.Initialised(
            content = content,
            error = error,
            headlineCategories = headlineCategories,
            isLoadingHeadlines = isLoadingHeadlines,
            selectedCategoryIndex = selectedCategoryIndex,
        )
    }
}

fun HeadlinesLoadError.toHomeScreenError(): HomeScreenError? = when (this) {
    HeadlinesLoadError.API_KEY_DISABLED,
    HeadlinesLoadError.API_KEY_EXHAUSTED,
    HeadlinesLoadError.API_KEY_INVALID -> HomeScreenError.NEWS_API_KEY

    HeadlinesLoadError.NETWORK -> HomeScreenError.NETWORK

    HeadlinesLoadError.RATE_LIMITED -> HomeScreenError.RATE_LIMITED

    HeadlinesLoadError.SOURCE_DOES_NOT_EXIST -> HomeScreenError.SOURCE_DOES_NOT_EXIST

    HeadlinesLoadError.SERVER -> HomeScreenError.SERVER
    else -> null
}

private fun HomeScreenError.toMessage(): HomeScreenMessage? = when (this) {
    HomeScreenError.NEWS_API_KEY -> HomeScreenMessage.INVALID_NEWS_API_KEY
    else -> null
}