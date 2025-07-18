package com.marcel.pna.ui.home

import com.marcel.pna.MainDispatcherRule
import com.marcel.pna.core.Result
import com.marcel.pna.headlines.domain.HeadlinesLoadError
import com.marcel.pna.headlines.domain.HeadlinesRepository
import com.marcel.pna.headlines.domain.HeadlinesUseCaseProvider
import com.marcel.pna.ui.home.HomeScreenUiState.Initialised
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `When SelectedHeadlineCategoryChanged intent received, Then selectedCategoryIndex changed`() =
        runTest {
            val viewModel = HomeScreenViewModel(
                headlinesUseCaseProvider = mockk(),
                userSettingsUseCaseProvider = mockk()
            )
            var state: Initialised? = null
            val uiStateCollectionJob = launch {
                viewModel.uiState.filterIsInstance<Initialised>().collect {
                    state = it
                }
            }
            viewModel.onIntent(HomeScreenIntent.SelectedHeadlineCategoryChanged(index = 4))
            advanceUntilIdle()
            assertEquals(
                expected = 4,
                actual = state?.selectedCategoryIndex
            )
            uiStateCollectionJob.cancel()
        }

    @Test
    fun `When PageHeadlinesLoad fails, Then state reflects corresponding screen error`() = runTest {
        // errors thrown
        val errorPairs = listOf(
            HeadlinesLoadError.API_KEY_DISABLED to HomeScreenError.NEWS_API_KEY,
            HeadlinesLoadError.API_KEY_EXHAUSTED to HomeScreenError.NEWS_API_KEY,
            HeadlinesLoadError.API_KEY_INVALID to HomeScreenError.NEWS_API_KEY,
        )
        // Mock GetHeadlinesUse
        val headlinesRepository = mockk<HeadlinesRepository> {
            coEvery {
                getHeadlines(any())
            } returnsMany errorPairs.map { Result.Failure(it.first) }
        }
        // Mock HeadlinesUseCaseProvider
        val headlinesUseCaseProvider = HeadlinesUseCaseProvider(
            backgroundDispatcher = mainDispatcherRule.testDispatcher,
            headlinesRepository = headlinesRepository
        )
        for ((loadError, screenError) in errorPairs) {
            // Create HomeScreenViewModel
            val viewModel = HomeScreenViewModel(
                headlinesUseCaseProvider = headlinesUseCaseProvider,
                userSettingsUseCaseProvider = mockk()
            )
            viewModel.onIntent(HomeScreenIntent.FirstPageHeadlinesLoad)
            advanceUntilIdle()
            val error = viewModel.uiState.filterIsInstance<Initialised>().first().error
            assertEquals(
                expected = screenError,
                actual = error,
                message = "Expected error $screenError for load error: $loadError"
            )
            coVerify(exactly = 1) { headlinesRepository.getHeadlines(any()) }
        }
    }
}

private val HomeScreenUiState.initialised: Initialised?
    get() = when (this) {
        is Initialised -> this
        else -> null
    }