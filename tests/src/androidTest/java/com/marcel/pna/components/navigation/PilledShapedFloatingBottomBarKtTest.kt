package com.marcel.pna.components.navigation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.marcel.pna.components.theme.primaryContainer
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test


class PilledShapedFloatingBottomBarKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenAListOfItemsThenDisplayExactItems() {
        val numberOfItems = 4
        val items = navigationItemsTestData.take(numberOfItems)

        composeTestRule.setContent {
            PilledShapedFloatingNavBar(
                backgroundColor = primaryContainer,
                items = items,
                onItemSelected = { _ -> },
            )
        }

        val tabs = composeTestRule.onNodeWithTag(
            PILL_NAV_ITEMS_TEST_TAG,
            useUnmergedTree = true
        )
            .onChildren()
            .filter(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        tabs.assertCountEquals(numberOfItems)
        (0 until numberOfItems).forEach { i ->
            tabs[i].assertNavItemDetailsAreCorrect(items[i])
        }
    }

    @Test
    fun givenAListOfItemsWhenItemTappedThenSelectionUpdatedAndCallbackCalled() {
        val numberOfItems = 4 // Todo: investigate why it doesn't work at 4
        val items = navigationItemsTestData.take(numberOfItems)
        val onItemSelected = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            PilledShapedFloatingNavBar(
                backgroundColor = primaryContainer,
                items = items,
                onItemSelected = onItemSelected,
            )
        }
        val tabs = composeTestRule.onNodeWithTag(
            PILL_NAV_ITEMS_TEST_TAG,
            useUnmergedTree = true
        )
            .onChildren()
            .filter(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        tabs.assertCountEquals(numberOfItems)
        // Verify initial selected item matches default index of 0
        checkNavItemSelections(
            expectedSelectedIndex = 0,
            numberOfItems = numberOfItems,
            tabs = tabs
        )
        val indexToSelect = numberOfItems.minus(1)
        val think = tabs[indexToSelect]
        think.performClick()

        // Verify onItemSelected is called
        verify(exactly = 1) { onItemSelected(indexToSelect) }
        confirmVerified(onItemSelected)

        // Verify selected item matches index
        checkNavItemSelections(
            expectedSelectedIndex = indexToSelect,
            numberOfItems = numberOfItems,
            tabs = tabs
        )
    }

    @Test
    fun givenASelectedItemIndexThenCorrespondingItemIsSelected() {
        val numberOfItems = 3
        val items = navigationItemsTestData.take(numberOfItems)

        composeTestRule.setContent {
            PilledShapedFloatingNavBar(
                backgroundColor = primaryContainer,
                items = items,
                selectedItemIndex = (numberOfItems - 1),
                onItemSelected = { _ -> },
            )
        }
        checkNavItemSelections(
            expectedSelectedIndex = (numberOfItems - 1),
            numberOfItems = numberOfItems,
            tabs = composeTestRule.onNodeWithTag(
                PILL_NAV_ITEMS_TEST_TAG,
                useUnmergedTree = true
            )
                .onChildren()
                .filter(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        )

    }

    private fun SemanticsNodeInteraction.assertNavItemDetailsAreCorrect(
        navigationItem: NavigationItem
    ) {
        onChildren().filterToOne(
            hasContentDescription(
                navigationItem.title,
                ignoreCase = true
            )
        ).assertExists()
    }

    private fun checkNavItemSelections(
        expectedSelectedIndex: Int,
        numberOfItems: Int,
        tabs: SemanticsNodeInteractionCollection
    ) {
        (0 until numberOfItems).forEach { i ->
            if (i == expectedSelectedIndex) {
                tabs[i].assertIsSelected()
            } else {
                tabs[i].assertIsNotSelected()
            }
        }
    }

}