package com.marcel.pna.ui.home

import com.marcel.pna.headlines.category.ArticleCategory

sealed class HomeScreenIntent {
    data class SelectedHeadlineCategoryChanged(val index: Int) : HomeScreenIntent()
}

data class HomeScreenModelState(
    val headlineCategories: List<ArticleCategory> = ArticleCategory.entries,
    val selectedCategoryIndex: Int = 0,
)

data class HomeScreenUiState(
    val headlineCategories: List<ArticleCategory>,
    val selectedCategoryIndex: Int,
)

fun HomeScreenModelState.toUiState(): HomeScreenUiState {
    return HomeScreenUiState(
        headlineCategories = headlineCategories,
        selectedCategoryIndex = selectedCategoryIndex
    )
}