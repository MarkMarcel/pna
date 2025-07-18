package com.marcel.pna.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcel.pna.headlines.category.ArticleCategory
import com.marcel.pna.headlines.domain.HeadlinesPage
import com.marcel.pna.headlines.domain.HeadlinesRequest
import com.marcel.pna.headlines.domain.HeadlinesRequest.Companion.nextPage
import com.marcel.pna.headlines.domain.HeadlinesUseCaseProvider
import com.marcel.pna.ui.home.HomeScreenModelState.Companion.initialised
import com.marcel.pna.usersettings.domain.usecases.UserSettingsUseCaseProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val headlinesUseCaseProvider: HeadlinesUseCaseProvider,
    private val userSettingsUseCaseProvider: UserSettingsUseCaseProvider
) : ViewModel() {
    val uiState: StateFlow<HomeScreenUiState> by lazy {
        modelState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = HomeScreenModelState.NotInitialised.toUiState()
            )
    }

    private val modelState =
        MutableStateFlow<HomeScreenModelState>(HomeScreenModelState.NotInitialised)
    private var headlinesLoadJob: Job? = null

    fun onIntent(intent: HomeScreenIntent) {
        when (intent) {
            is HomeScreenIntent.ErrorHandled -> {
                onErrorHandled()
            }

            is HomeScreenIntent.FirstPageHeadlinesLoad -> {
                loadHeadlinesFirstPage()
            }

            is HomeScreenIntent.Initialisation -> {
                observeUserSettings()
            }

            is HomeScreenIntent.NextPageHeadlinesLoad -> {
                loadHeadlinesNextPage()
            }

            is HomeScreenIntent.SelectedHeadlineCategoryChanged -> {
                modelState.update { state ->
                    state.initialised.copy(selectedCategoryIndex = intent.index)
                }
            }
        }
    }

    private fun addError(error: HomeScreenError) {
        modelState.update { state ->
            state.initialised.run {
                copy(errors = errors + error)
            }
        }
    }

    private fun loadHeadlinesFirstPage() {
        headlinesLoadJob?.cancel()
        headlinesLoadJob = viewModelScope.launch {
            modelState.update { state ->
                state.initialised.copy(isLoadingHeadlines = true)
            }
            loadHeadlinesPage(request = modelState.value.initialised.getHeadlinesBaseRequest())
        }
    }

    private fun loadHeadlinesNextPage() {
        headlinesLoadJob = viewModelScope.launch {
            modelState.update { state ->
                state.initialised.copy(isLoadingHeadlines = true)
            }
            val request = modelState.value.initialised.getHeadlinesBaseRequest().nextPage
            loadHeadlinesPage(request = request)
        }
    }

    private suspend fun loadHeadlinesPage(request: HeadlinesRequest) {
        headlinesUseCaseProvider.getHeadlinesUseCase
            .run(request = request)
            .fold(
                onFailure = { error ->
                    modelState.update { state ->
                        state.initialised.copy(isLoadingHeadlines = false)
                    }
                    error.toHomeScreenError()?.let {
                        addError(error = it)
                    }
                },
                onSuccess = { headlinesPage ->
                    modelState.update {
                        it.initialised.run {
                            copy(
                                isLoadingHeadlines = false,
                                pagedHeadlines = HeadlinesPage(
                                    headlines = pagedHeadlines.headlines + headlinesPage.headlines,
                                    nextPage = headlinesPage.nextPage
                                )
                            )
                        }
                    }
                }
            )

    }

    private fun observeUserSettings() {
        viewModelScope.launch {
            userSettingsUseCaseProvider.getUserSettingsUseCase.run().collectLatest { userSettings ->
                modelState.update { state ->
                    val stateToUpdate = when (state) {
                        is HomeScreenModelState.Initialised -> state
                        is HomeScreenModelState.NotInitialised -> {
                            HomeScreenModelState.Initialised()
                        }
                    }
                    stateToUpdate.copy(
                        loadTrendingHeadlinesBy = userSettings.loadTrendingHeadlinesBy
                    )
                }
                onIntent(HomeScreenIntent.FirstPageHeadlinesLoad)
            }
        }
    }

    private fun onErrorHandled() {
        viewModelScope.launch {
            modelState.update { state ->
                state.initialised.run {
                    copy(
                        errors = errors.dropLast(1)
                    )
                }
            }
        }
    }

    private fun HomeScreenModelState.Initialised.getHeadlinesBaseRequest(): HeadlinesRequest {
        return when (val selectedCategory = headlineCategories[selectedCategoryIndex]) {
            ArticleCategory.TRENDING -> {
                HeadlinesRequest.Trending(
                    loadTrendingHeadlinesBy = loadTrendingHeadlinesBy
                )
            }

            else -> {
                HeadlinesRequest.Category(
                    category = selectedCategory
                )
            }
        }
    }
}