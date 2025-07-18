package com.marcel.pna.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcel.pna.PNAMLogo
import com.marcel.pna.R
import com.marcel.pna.components.Centered
import com.marcel.pna.components.headline.Headline
import com.marcel.pna.components.headline.HeadlineCategories
import com.marcel.pna.components.headline.HeadlineCategory
import com.marcel.pna.components.headline.Headlines
import com.marcel.pna.components.headline.HeadlinesLoadingState
import com.marcel.pna.core.capitaliseWithLocal
import com.marcel.pna.headlines.category.ArticleCategory
import com.marcel.pna.headlines.domain.Article
import com.marcel.pna.theme.PNAMTheme
import com.marcel.pna.theme.baseSpacing
import com.marcel.pna.theme.baseSpacingDiv2
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
            when (val state = uiState) {
                is HomeScreenUiState.Initialised -> {
                    HomeScreenTopBar(
                        headlineCategories = state.headlineCategories, // Todo: observe compositions
                        selectedCategoryIndex = state.selectedCategoryIndex,
                        onCategorySelected = { index ->
                            viewModel.onIntent(
                                HomeScreenIntent.SelectedHeadlineCategoryChanged(index = index)
                            )
                        },
                        navigationIcon = navigationIcon
                    )
                }

                is HomeScreenUiState.NotInitialised -> {}
            }
        }
    ) { paddingValues ->
        HomeScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            state = uiState,
            onErrorHandled = { viewModel.onIntent(HomeScreenIntent.ErrorHandled) },
            onGoToUserSettings = onGoToUserSettings,
            onInitialise = {
                viewModel.onIntent(HomeScreenIntent.Initialisation)
            }
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
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeScreenUiState,
    onErrorHandled: () -> Unit,
    onGoToUserSettings: () -> Unit,
    onInitialise: () -> Unit,
) {
    Surface {
        when (state) {
            is HomeScreenUiState.NotInitialised -> {
                Centered(
                    modifier = modifier
                ) {
                    CircularProgressIndicator()
                }
                LaunchedEffect(Unit) {
                    onInitialise()
                }
            }

            is HomeScreenUiState.Initialised -> {
                HomeScreenInitialisedContent(
                    modifier = modifier,
                    state = state,
                    onErrorHandled = onErrorHandled,
                    onGoToUserSettings = onGoToUserSettings
                )
            }
        }
    }
}

@Composable
private fun HomeScreenInitialisedContent(
    modifier: Modifier = Modifier,
    state: HomeScreenUiState.Initialised,
    onErrorHandled: () -> Unit,
    onGoToUserSettings: () -> Unit,
) {
    Crossfade(
        modifier = modifier,
        targetState = state.content
    ) { content ->
        when (content) {
            is HomeScreenContent.Headlines -> {
                Headlines(
                    headlines = content.pagedHeadlines.headlines.map { it.toHeadline() },
                    loadingState = if (state.isLoadingHeadlines) HeadlinesLoadingState.Loading else HeadlinesLoadingState.NotLoading,
                    onHeadlineTapped = {},
                    onOpenHeadlineUrl = {},
                    onSaveHeadline = {}
                )
            }

            is HomeScreenContent.Message -> {
                NewsApiAccessMessage(
                    modifier = modifier,
                    onErrorHandled = onErrorHandled,
                    onGoToUserSettings = onGoToUserSettings
                )
            }
        }

    }
}

@Composable
private fun NewsApiAccessMessage(
    modifier: Modifier = Modifier,
    onErrorHandled: () -> Unit,
    onGoToUserSettings: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = baseSpacing),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(
                R.string.news_api_access_error_heading,
                stringResource(R.string.news_api_service)
            ).capitaliseWithLocal(),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(baseSpacingDiv2))
        Text(
            stringResource(
                R.string.news_api_access_error_prompt,
                stringResource(R.string.news_api_service)
            ).capitaliseWithLocal()
        )
        Spacer(modifier = Modifier.height(baseSpacing))
        Button(
            onClick = {
                onGoToUserSettings()
                onErrorHandled()
            }
        ) {
            Text(text = stringResource(R.string.settings_btn))
        }
    }
}

@Composable
private fun Article.toHeadline(): Headline {
    return Headline(
        authorName = author,
        description = description,
        source = source.name,
        timeStamp = publishedAt,
        title = title,
        url = url
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
fun HomeScreenInitialisedContentPreview() {
    PNAMTheme {
        HomeScreenInitialisedContent(
            state = HomeScreenModelState.Initialised().toUiState() as HomeScreenUiState.Initialised,
            onErrorHandled = {},
            onGoToUserSettings = {},
        )
    }
}