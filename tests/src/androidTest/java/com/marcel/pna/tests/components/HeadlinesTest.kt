package com.marcel.pna.tests.components

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.marcel.pna.components.headline.CURRENT_HEADLINE_LOADING_TEST_TAG
import com.marcel.pna.components.headline.CURRENT_HEADLINE_TITLE_TEST_TAG
import com.marcel.pna.components.headline.HEADLINES_FINISHED_TEST_TAG
import com.marcel.pna.components.headline.HEADLINE_CARDS_TEST_TAG
import com.marcel.pna.components.headline.Headlines
import com.marcel.pna.components.headline.HeadlinesData
import com.marcel.pna.components.headline.HeadlinesLoadingState
import com.marcel.pna.components.headline.HeadlinesState
import com.marcel.pna.components.headline.NEXT_HEADLINE_LOADING_TEST_TAG
import com.marcel.pna.components.headline.NEXT_HEADLINE_TITLE_TEST_TAG
import com.marcel.pna.components.headline.headlinesTestData
import com.marcel.pna.components.kotlinextensions.toTitleCase
import org.junit.Rule
import org.junit.Test

// Todo: check memory limits and adjust gradle properties for CI/CD
// Todo: Test when end was but was loading
class HeadlinesTest {
    private val testTags = mapOf(
        CURRENT_HEADLINE_LOADING_TEST_TAG to CURRENT_HEADLINE_LOADING_TEST_TAG,
        CURRENT_HEADLINE_TITLE_TEST_TAG to CURRENT_HEADLINE_TITLE_TEST_TAG,
        HEADLINE_CARDS_TEST_TAG to HEADLINE_CARDS_TEST_TAG,
        HEADLINES_FINISHED_TEST_TAG to HEADLINES_FINISHED_TEST_TAG,
        NEXT_HEADLINE_LOADING_TEST_TAG to NEXT_HEADLINE_LOADING_TEST_TAG,
        NEXT_HEADLINE_TITLE_TEST_TAG to NEXT_HEADLINE_TITLE_TEST_TAG
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenCurrentAndNextHeadlineIndicesNotLastWhenSwipedLeftThenCurrentAndNextHeadlineIndicesIncrementedWithinBounds() {
        composeTestRule.setContent {
            val headlinesData = HeadlinesData(HeadlinesLoadingState.Loaded, headlinesTestData)
            val headlinesState = remember { HeadlinesState(headlinesTestData.size) }
            Headlines(
                headlinesData = headlinesData,
                state = headlinesState,
                testTags = testTags
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
    fun givenCurrentAndNextHeadlineIndicesNotFirstWhenSwipedRightThenCurrentAndNextHeadlineIndicesDecrementedWithinBounds() {
        composeTestRule.setContent {
            val headlinesData = HeadlinesData(HeadlinesLoadingState.Loaded, headlinesTestData)
            val headlinesState = remember {
                HeadlinesState(initialDataSize = headlinesTestData.size, startIndex = 1)
            }
            Headlines(
                headlinesData = headlinesData,
                state = headlinesState,
                testTags = testTags
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
    fun givenCurrentHeadlineIsFirstWhenSwipedRightThenNoAction() {
        composeTestRule.setContent {
            val headlinesData = HeadlinesData(HeadlinesLoadingState.Loaded, headlinesTestData)
            val headlinesState = remember { HeadlinesState(headlinesTestData.size) }
            Headlines(
                headlinesData = headlinesData,
                state = headlinesState,
                testTags = testTags
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
}