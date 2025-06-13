package com.marcel.pna.ui.home

import com.marcel.pna.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            val viewModel = HomeScreenViewModel()
            var state: HomeScreenUiState? = null
            val uiStateCollectionJob = launch {
                viewModel.uiState.collect {
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

}