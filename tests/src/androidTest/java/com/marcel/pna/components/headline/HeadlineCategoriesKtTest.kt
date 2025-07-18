package com.marcel.pna.components.headline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class HeadlineCategoriesKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenAListOfCategories_ThenCorrectNumberAndDataIsDisplayed() {
        val selectedIndex = 0
        composeTestRule.setContent {
            HeadlineCategories(
                headlineCategories = headlineCategoriesTestData,
                contentColor = Color.Black,
                selectedCategoryIndex = selectedIndex,
                onCategorySelected = {}
            )
        }
        // Verify correct number of categories
        composeTestRule
            .onAllNodes(hasClickAction())
            .assertCountEquals(headlineCategoriesTestData.size)
        // Verify category labels
        headlineCategoriesTestData.forEach { category ->
            composeTestRule.onNodeWithText(category.name, ignoreCase = true)
                .assertExists("Tab with label ${category.name} not found")
        }
        // Verify selected category
        composeTestRule.onNodeWithText(
            headlineCategoriesTestData[selectedIndex].name,
            ignoreCase = true
        )
            .assertIsSelected()
    }

    @Test
    fun givenDisplayedCategories_WhenCategoryIsSelected_ThenCorrectCategoryIsSelected() {
        val selectedIndex = 0
        val onCategorySelected = mockk<(Int) -> Unit>(relaxed = true)
        composeTestRule.setContent {
            HeadlineCategories(
                headlineCategories = headlineCategoriesTestData,
                contentColor = Color.Black,
                selectedCategoryIndex = selectedIndex,
                onCategorySelected = onCategorySelected
            )
        }
        // Click on a category
        val newSelectedIndex = headlineCategoriesTestData.lastIndex
        composeTestRule.onNode(
            hasClickAction().and(
                hasText(
                    headlineCategoriesTestData[newSelectedIndex].name,
                    ignoreCase = true
                )
            )
        )
            .performScrollTo()
            .performClick()
        // Verify category is selected
        composeTestRule.onNodeWithText(
            headlineCategoriesTestData[newSelectedIndex].name,
            ignoreCase = true
        )
            .assertIsSelected()
        // Verify old category is no longer selected
        composeTestRule.onNodeWithText(
            headlineCategoriesTestData[selectedIndex].name,
            ignoreCase = true
        ).assertIsNotSelected()
        // Verify onCategorySelected is called with the correct index
        verify(exactly = 1) { onCategorySelected(newSelectedIndex) }
        confirmVerified(onCategorySelected)
    }
}