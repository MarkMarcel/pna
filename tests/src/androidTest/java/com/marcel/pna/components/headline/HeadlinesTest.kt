package com.marcel.pna.components.headline

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.marcel.pna.components.R
import com.marcel.pna.components.getStringResource
import com.marcel.pna.components.hasAnyCustomActionLabel
import com.marcel.pna.components.swipeLeft
import com.marcel.pna.components.swipeRight
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Todo: check memory limits and adjust gradle properties for CI/CD
// Todo: Test when end was but was loading
class HeadlinesTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    lateinit var previousHeadlineActionLabel: String

    @Before
    fun setStringResources() {
        previousHeadlineActionLabel = getStringResource(R.string.previous_headline_action_label)
    }

    @Test
    fun givenCurrentAndNextHeadlineIndicesNotFirstWhenSwipedRightThenCurrentAndNextHeadlineIndicesDecrementedWithinBounds() {
        val currentIndex = headlinesTestData.lastIndex

        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState(currentIndex)
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        composeTestRule
        composeTestRule.onNode(
            hasAnyCustomActionLabel(previousHeadlineActionLabel)
        ).swipeRight()
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[currentIndex - 1])
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[currentIndex])
    }

    @Test
    fun givenCurrentAndNextHeadlineIndicesNotLastWhenSwipedLeftThenCurrentAndNextHeadlineIndicesIncrementedWithinBounds() {
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        // Dismiss current headline
        composeTestRule.onNode(
            hasAnyCustomActionLabel(previousHeadlineActionLabel)
        ).swipeLeft()
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[1])
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[2])
    }

    @Test
    fun givenCurrentHeadlineDisplayedWhenActionsInitiatedThenCallbacksCalled() {
        val openHeadlineContentDescription =
            getStringResource(R.string.open_headline_url_content_description)
        val saveHeadlineContentDescription =
            getStringResource(R.string.save_headline_content_description)
        val onHeadlineTapped = mockk<(Int) -> Unit>(relaxed = true)
        val onSaveHeadline = mockk<(Int) -> Unit>(relaxed = true)
        val onOpenHeadlineUrl = mockk<(Int) -> Unit>(relaxed = true)
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                onHeadlineTapped = onHeadlineTapped,
                onSaveHeadline = onSaveHeadline,
                onOpenHeadlineUrl = onOpenHeadlineUrl,
            )
        }
        val currentHeadline = composeTestRule.onNodeWithTag(CURRENT_HEADLINE_TEST_TAG)
        currentHeadline.performClick()
        currentHeadline.onChildren()
            .filterToOne(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(saveHeadlineContentDescription)
                )
            )
            .performClick()
        currentHeadline.onChildren()
            .filterToOne(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf(openHeadlineContentDescription)
                )
            )
            .performClick()
        verifySequence {
            onHeadlineTapped(0)
            onSaveHeadline(0)
            onOpenHeadlineUrl(0)
        }
    }

    @Test
    fun givenCurrentHeadlineIsFirstWhenSwipedRightThenNoChange() {
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState()
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        // Verify current headline is first in list
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[0])

        // Perform swipe
        composeTestRule.onNodeWithTag(CURRENT_HEADLINE_TEST_TAG).swipeRight()

        // Verify there's no change
        composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(headlinesTestData[0])
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
                onHeadlineTapped = { _ -> },
                onSaveHeadline = { _ -> },
                onOpenHeadlineUrl = { _ -> }
            )
        }
        val currentHeadline = composeTestRule.onNodeWithTag(
            CURRENT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
        // Verify current headline is last headline in initialHeadlines
        currentHeadline.assertHeadlineDetailsAreCorrect(initialHeadlines[initialHeadlines.lastIndex])
        // Verify next headline is showing loading card
        composeTestRule.onNodeWithTag(NEXT_HEADLINE_LOADING_TEST_TAG)
            .assertExists(errorMessageOnFail = "Next headline loading indicator not displayed")

        // update list
        headlines.update { state -> state + loadingHeadlines }

        // Verify items displayed on screen updated
        currentHeadline.assertHeadlineDetailsAreCorrect(initialHeadlines[initialHeadlines.lastIndex])
        composeTestRule.onNodeWithTag(
            NEXT_HEADLINE_TEST_TAG,
            useUnmergedTree = true
        )
            .assertHeadlineDetailsAreCorrect(loadingHeadlines[0])
    }

    @Test
    fun givenCurrentHeadlineIsLastAndNotLoadingWhenSwipedLeftThenFinishedUIDisplayed() {
        composeTestRule.setContent {
            val headlinesState = rememberHeadlinesState(startIndex = headlinesTestData.lastIndex)
            Headlines(
                headlines = headlinesTestData,
                loadingState = HeadlinesLoadingState.NotLoading,
                state = headlinesState,
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
        composeTestRule.onNode(
            hasAnyCustomActionLabel(previousHeadlineActionLabel)
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

private fun SemanticsNodeInteraction.assertHeadlineDetailsAreCorrect(
    headline: Headline
) {
    with(onChildren()) {
        assertAny(hasText(headline.authorName, ignoreCase = true))
        assertAny(hasText(headline.source, ignoreCase = true))
        assertAny(hasText(headline.description, ignoreCase = true))
        assertAny(hasText(headline.title, ignoreCase = true))
    }
}