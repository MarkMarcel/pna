package com.marcel.pna.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcel.pna.PNAMLogo
import com.marcel.pna.R
import com.marcel.pna.components.Centered
import com.marcel.pna.components.headline.HeadlineCategories
import com.marcel.pna.components.headline.HeadlineCategory
import com.marcel.pna.headlines.category.ArticleCategory
import com.marcel.pna.theme.PNAMTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
    viewModel: HomeScreenViewModel = koinViewModel(),
    onGoToUserSettings: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeScreenTopBar(
                headlineCategories = uiState.headlineCategories, // Todo: observe compositions
                selectedCategoryIndex = uiState.selectedCategoryIndex,
                onCategorySelected = { index ->
                    viewModel.onIntent(
                        HomeScreenIntent.SelectedHeadlineCategoryChanged(index = index)
                    )
                },
                navigationIcon = navigationIcon
            )
        }
    ) { paddingValues ->
        HomeScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            onGoToUserSettings = onGoToUserSettings
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenTopBar(
    modifier: Modifier = Modifier,
    headlineCategories: List<ArticleCategory>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    navigationIcon: @Composable () -> Unit,
) {
    val uiHeadlineCategories = headlineCategories.map { it.toHeadlineCategory() }
    MediumTopAppBar(
        modifier = modifier,
        title = {
            HeadlineCategories(
                headlineCategories = uiHeadlineCategories,
                contentColor = TopAppBarDefaults.mediumTopAppBarColors().titleContentColor,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = onCategorySelected
            )
        },
        navigationIcon = navigationIcon,
    )
}

@Composable
private fun ArticleCategory.toHeadlineCategory(): HeadlineCategory {
    val name = when (this) {
        ArticleCategory.TRENDING -> stringResource(R.string.trending_label)
        ArticleCategory.BUSINESS -> stringResource(R.string.business_label)
        ArticleCategory.ENTERTAINMENT -> stringResource(R.string.entertainment_label)
        ArticleCategory.GENERAL -> stringResource(R.string.general_label)
        ArticleCategory.HEALTH -> stringResource(R.string.health_label)
        ArticleCategory.SCIENCE -> stringResource(R.string.science_label)
        ArticleCategory.SPORTS -> stringResource(R.string.sports_label)
        ArticleCategory.TECHNOLOGY -> stringResource(R.string.technology_label)
    }
    return HeadlineCategory(name = name)
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onGoToUserSettings: () -> Unit,
) {
    Surface {
        Centered(modifier) {
            Button(onClick = onGoToUserSettings) {
                Text(text = "Settings")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenTopBarPreview() {
    PNAMTheme {
        HomeScreenTopBar(
            headlineCategories = ArticleCategory.entries,
            selectedCategoryIndex = 0,
            onCategorySelected = { _ -> },
            navigationIcon = { PNAMLogo() }
        )
    }
}

@Preview
@Composable
fun HomeScreenContentPreview() {
    PNAMTheme {
        HomeScreenContent(
            onGoToUserSettings = {}
        )
    }
}