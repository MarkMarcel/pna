package com.marcel.pna.tests.components

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.marcel.pna.components.headline.CURRENT_HEADLINE_LOADING_TEST_TAG
import com.marcel.pna.components.headline.CURRENT_HEADLINE_OPEN_URL_TEST_TAG
import com.marcel.pna.components.headline.CURRENT_HEADLINE_SAVE_TEST_TAG
import com.marcel.pna.components.headline.CURRENT_HEADLINE_TEST_TAG
import com.marcel.pna.components.headline.CURRENT_HEADLINE_TITLE_TEST_TAG
import com.marcel.pna.components.headline.HEADLINES_FINISHED_TEST_TAG
import com.marcel.pna.components.headline.HEADLINE_CARDS_TEST_TAG
import com.marcel.pna.components.headline.Headlines
import com.marcel.pna.components.headline.HeadlinesLoadingState
import com.marcel.pna.components.headline.NEXT_HEADLINE_LOADING_TEST_TAG
import com.marcel.pna.components.headline.NEXT_HEADLINE_TEST_TAG
import com.marcel.pna.components.headline.NEXT_HEADLINE_TITLE_TEST_TAG
import com.marcel.pna.components.headline.headlinesTestData
import com.marcel.pna.components.headline.rememberHeadlinesState
import com.marcel.pna.components.kotlinextensions.toTitleCase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Rule
import org.junit.Test

// Todo: check memory limits and adjust gradle properties for CI/CD
// Todo: Test when end was but was loading
class HeadlinesTest {
    private val testTags = mapOf(
        CURRENT_HEADLINE_TEST_TAG to CURRENT_HEADLINE_TEST_TAG,
        CURRENT_HEADLINE_LOADING_TEST_TAG to CURRENT_HEADLINE_LOADING_TEST_TAG,
        CURRENT_HEADLINE_OPEN_URL_TEST_TAG to CURRENT_HEADLINE_OPEN_URL_TEST_TAG,
        CURRENT_HEADLINE_SAVE_TEST_TAG to CURRENT_HEADLINE_SAVE_TEST_TAG,
        CURRENT_HEADLINE_TITLE_TEST_TAG to CURRENT_HEADLINE_TITLE_TEST_TAG,
        HEADLINE_CARDS_TEST_TAG to HEADLINE_CARDS_TEST_TAG,
        HEADLINES_FINISHED_TEST_TAG to HEADLINES_FINISHED_TEST_TAG,
        NEXT_HEADLINE_LOADING_TEST_TAG to NEXT_HEADLINE_LOADING_TEST_TAG,
        NEXT_HEADLINE_TEST_TAG to NEXT_HEADLINE_TEST_TAG,
        NEXT_HEADLINE_TITLE_TEST_TAG to NEXT_HEADLINE_TITLE_TEST_TAG
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenCurrentAndNextHeadlineIndicesNotFirstWhenSwipedRightThenCurrentAndNextHeadlineIndicesDecrementedWithinBounds() {
        val currentIndex = headlinesTestData.lastIndex
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState(currentIndex)
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        composeTestRule.onNodeWithTag(
            HEADLINE_CARDS_TEST_TAG,
            useUnmergedTree = true
        ).swipeRight()
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[currentIndex - 1].title.toTitleCase())
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[currentIndex].title.toTitleCase())
    }

    @Test
    fun givenCurrentAndNextHeadlineIndicesNotLastWhenSwipedLeftThenCurrentAndNextHeadlineIndicesIncrementedWithinBounds() {
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        // Dismiss current headline
        composeTestRule.onNodeWithTag(
            HEADLINE_CARDS_TEST_TAG,
            useUnmergedTree = true
        ).swipeLeft()
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[1].title.toTitleCase())
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[2].title.toTitleCase())
    }

    @Test
    fun givenCurrentHeadlineDisplayedWhenActionsInitiatedThenCallbacksCalled() {
        val onHeadlineTapped = mockk<(Int) -> Unit>(relaxed = true)
        val onSaveHeadline = mockk<(Int) -> Unit>(relaxed = true)
        val onOpenHeadlineUrl = mockk<(Int) -> Unit>(relaxed = true)
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = onHeadlineTapped,
                onSaveHeadline = onSaveHeadline,
                onOpenHeadlineUrl = onOpenHeadlineUrl,
            )
        }
        composeTestRule.onNodeWithTag(CURRENT_HEADLINE_TEST_TAG).performClick()
        verify(exactly = 1) { onHeadlineTapped(0) }
        composeTestRule.onNodeWithTag(CURRENT_HEADLINE_OPEN_URL_TEST_TAG).performClick()
        verify(exactly = 1) { onOpenHeadlineUrl(0) }
        composeTestRule.onNodeWithTag(CURRENT_HEADLINE_SAVE_TEST_TAG).performClick()
        verify(exactly = 1) { onHeadlineTapped(0) }
    }

    @Test
    fun givenCurrentHeadlineIsFirstWhenSwipedRightThenNoChange() {
        composeTestRule.setContent {

            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        composeTestRule.onNodeWithTag(
            HEADLINE_CARDS_TEST_TAG,
            useUnmergedTree = true
        ).swipeRight()
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[0].title.toTitleCase())
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(headlinesTestData[1].title.toTitleCase())
    }

    @Test
    fun givenHeadlinesAndLoadingStateThenCorrectUIIsDisplayed() {
        if (headlinesTestData.size < 2) throw Exception("HeadlinesTestData must have at least two headlines")
        val initialHeadlines =
            headlinesTestData.subList(fromIndex = 0, toIndex = (headlinesTestData.size / 2 + 1))
        val loadingHeadlines =
            headlinesTestData.subList(
                fromIndex = (headlinesTestData.size / 2 + 1),
                toIndex = headlinesTestData.size
            )

        // Initialise flows to allow updates
        val headlines = MutableStateFlow(initialHeadlines)
        val loadingState = MutableStateFlow(HeadlinesLoadingState.Loading)

        composeTestRule.setContent {
            // Set startIndex to lastIndex to ensure loading state is displayed
            val headlinesState = rememberHeadlinesState(startIndex = initialHeadlines.lastIndex)
            val headlines = headlines.collectAsState()
            val loadingState = loadingState.collectAsState()
            Headlines(
                headlines = headlines.value,
                loadingState = loadingState.value,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        // Verify current headline is last headline in initialHeadlines
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(initialHeadlines[initialHeadlines.lastIndex].title.toTitleCase())
        // Verify next headline is showing loading card
        composeTestRule.onNodeWithTag(NEXT_HEADLINE_LOADING_TEST_TAG)
            .assertExists(errorMessageOnFail = "Next headline loading indicator not displayed")
        headlines.update { state -> state + loadingHeadlines }

        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(initialHeadlines[initialHeadlines.lastIndex].title.toTitleCase())
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TITLE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertTextEquals(loadingHeadlines[0].title.toTitleCase())
    }

    @Test
    fun givenCurrentHeadlineIsLastAndNotLoadingWhenSwipedLeftThenFinishedUIDisplayed() {
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState(startIndex = headlinesTestData.lastIndex)
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                testTags = testTags,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> },
            )
        }
        // Verify current headline exists while next headline and finished UI don't exist
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
        ).assertExists(errorMessageOnFail = "Current headline not displayed")
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TEST_TAG,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            HEADLINES_FINISHED_TEST_TAG,
        ).assertDoesNotExist()

        // Dismiss current headline
        composeTestRule.onNodeWithTag(
            HEADLINE_CARDS_TEST_TAG,
            useUnmergedTree = true
        ).swipeLeft()
        // Verify current and next headline don't exist and finished UI exists
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TEST_TAG,
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(
            HEADLINES_FINISHED_TEST_TAG,
        ).assertExists(errorMessageOnFail = "Headlines finished UI not displayed")
    }
}