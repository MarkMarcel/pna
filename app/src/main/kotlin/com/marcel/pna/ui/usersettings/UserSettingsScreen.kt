package com.marcel.pna.ui.usersettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcel.pna.AppError
import com.marcel.pna.R
import com.marcel.pna.components.Centered
import com.marcel.pna.components.Prompt
import com.marcel.pna.components.PromptType
import com.marcel.pna.components.kotlinextensions.toTitleCase
import com.marcel.pna.components.theme.baseSpacingDiv2
import com.marcel.pna.core.Result
import com.marcel.pna.core.capitaliseWithLocal
import com.marcel.pna.core.rememberDeviceLanguageCode
import com.marcel.pna.theme.PNAMTheme
import com.marcel.pna.theme.baseSpacing
import com.marcel.pna.theme.baseSpacingDiv4
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserSettingsScreen(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
    viewModel: UserSettingsScreenViewModel = koinViewModel(),
    onOpenExternalUrl: (String) -> Result<AppError, Unit>,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val languageCode = rememberDeviceLanguageCode()
    Scaffold(
        modifier = modifier,
        topBar = {
            UserSettingsTopBar(
                navigationIcon = navigationIcon
            )
        }
    ) { contentPadding ->
        UserSettingsContent(
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
            state = uiState,
            onErrorHandled = {
                viewModel.onIntent(UserSettingsScreenIntent.ErrorHandled)
            },
            onLoadData = {
                viewModel.onIntent(UserSettingsScreenIntent.LoadData(languageCode = languageCode))
            },
            onNewsApiKeyChanged = { updatedApiKey ->
                viewModel.onIntent(
                    UserSettingsScreenIntent.UpdateNewsApiKey(updatedApiKey = updatedApiKey)
                )
            },
            onOpenExternalUrl = onOpenExternalUrl,
            onSetLoadTrendingHeadlinesBy = { loadTrendingHeadlinesBy ->
                viewModel.onIntent(
                    UserSettingsScreenIntent.SetLoadTrendingHeadlinesBy(
                        loadTrendingHeadlinesBy
                    )
                )
            },
            onSetNewsApiKey = { apiKey ->
                viewModel.onIntent(
                    UserSettingsScreenIntent.SetNewsApiKey(apiKey = apiKey)
                )
            },
            onSetTrendingHeadlinesCountry = { country ->
                viewModel.onIntent(
                    UserSettingsScreenIntent.SetTrendingHeadlinesCountry(
                        country = country
                    )
                )
            },
            onUpdateCountries = {
                viewModel.onIntent(UserSettingsScreenIntent.UpdateCountries)
            }
        )
        LaunchedEffect(languageCode) {
            viewModel.onIntent(UserSettingsScreenIntent.SetLanguageCode(languageCode = languageCode))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSettingsTopBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = navigationIcon,
        title = {
            Text(stringResource(R.string.user_settings_screen_title).toTitleCase())
        },
    )
}

@Composable
private fun UserSettingsContent(
    modifier: Modifier = Modifier,
    state: UserSettingsScreenUiState,
    onErrorHandled: () -> Unit,
    onLoadData: () -> Unit,
    onNewsApiKeyChanged: (String) -> Unit,
    onOpenExternalUrl: (String) -> Result<AppError, Unit>,
    onSetLoadTrendingHeadlinesBy: (LoadTrendingHeadlinesBySelection) -> Unit,
    onSetNewsApiKey: (String) -> Unit,
    onSetTrendingHeadlinesCountry: (UiCountry) -> Unit,
    onUpdateCountries: () -> Unit,
) {
    Surface {
        when (state) {
            is UserSettingsScreenUiState.NotInitialised -> {
                Centered(
                    modifier = modifier
                ) {
                    CircularProgressIndicator()
                }
                LaunchedEffect(Unit) {
                    onLoadData()
                }
            }

            is UserSettingsScreenUiState.Initialised -> {
                UserSettingsInitialisedContent(
                    modifier = modifier,
                    state = state,
                    onErrorHandled = onErrorHandled,
                    onOpenExternalUrl = onOpenExternalUrl,
                    onNewsApiKeyChanged = onNewsApiKeyChanged,
                    onSetLoadTrendingHeadlinesBy = onSetLoadTrendingHeadlinesBy,
                    onSetNewsApiKey = onSetNewsApiKey,
                    onSetTrendingHeadlinesCountry = onSetTrendingHeadlinesCountry,
                    onUpdateCountries = onUpdateCountries,
                )
            }
        }
    }
}


@Composable
private fun UserSettingsInitialisedContent(
    modifier: Modifier = Modifier,
    state: UserSettingsScreenUiState.Initialised,
    onErrorHandled: () -> Unit,
    onOpenExternalUrl: (String) -> Result<AppError, Unit>,
    onNewsApiKeyChanged: (String) -> Unit,
    onSetNewsApiKey: (String) -> Unit,
    onSetLoadTrendingHeadlinesBy: (LoadTrendingHeadlinesBySelection) -> Unit,
    onSetTrendingHeadlinesCountry: (UiCountry) -> Unit,
    onUpdateCountries: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = baseSpacing)
    ) {
        item {
            HeadlinesSettings(
                areCountriesUpdating = state.areCountriesUpdating,
                countries = state.countries,
                error = state.error,
                loadTrendingHeadlinesBy = state.loadTrendingHeadlinesBy,
                selectedCountry = state.country,
                onErrorHandled = onErrorHandled,
                onSetLoadTrendingHeadlinesBy = onSetLoadTrendingHeadlinesBy,
                onSetTrendingHeadlinesCountry = onSetTrendingHeadlinesCountry,
                onUpdateCountries = onUpdateCountries
            )
        }
        item {
            NewsApiServiceSettings(
                isSettingNewsApiKey = state.isSettingNewsApiKey,
                newsApiKey = state.newsApiKey,
                onGenerateApiKey = { onOpenExternalUrl(state.generateNewsApiUrl) },
                onNewsApiKeyChanged = onNewsApiKeyChanged,
                onSaveNewsApiKey = onSetNewsApiKey
            )
        }
    }
}

@Composable
private fun HeadlinesSettings(
    modifier: Modifier = Modifier,
    areCountriesUpdating: Boolean,
    countries: List<UiCountry>,
    selectedCountry: UiCountry?,
    error: UserSettingsScreenError?,
    loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
    onErrorHandled: () -> Unit,
    onSetLoadTrendingHeadlinesBy: (LoadTrendingHeadlinesBySelection) -> Unit,
    onSetTrendingHeadlinesCountry: (UiCountry) -> Unit,
    onUpdateCountries: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                bottom = baseSpacing,
                start = baseSpacing,
                end = baseSpacing
            )
    ) {
        SettingsGroupTitle(title = stringResource(R.string.headlines_heading).toTitleCase())
        Spacer(modifier = Modifier.height(baseSpacing))
        LoadTrendingHeadlinesBySelector(
            loadTrendingHeadlinesBy = loadTrendingHeadlinesBy,
            onOptionSelected = onSetLoadTrendingHeadlinesBy
        )
        AnimatedVisibility(loadTrendingHeadlinesBy == LoadTrendingHeadlinesBySelection.Country) {
            CountrySelector(
                areCountriesUpdating = areCountriesUpdating,
                countries = countries,
                selectedCountry = selectedCountry,
                error = error,
                onCountrySelected = onSetTrendingHeadlinesCountry,
                onErrorHandled = onErrorHandled,
                onUpdateCountries = onUpdateCountries,
            )
        }
    }
}

@Composable
private fun NewsApiServiceSettings(
    modifier: Modifier = Modifier,
    isSettingNewsApiKey: Boolean,
    newsApiKey: String,
    onGenerateApiKey: () -> Unit,
    onNewsApiKeyChanged: (String) -> Unit,
    onSaveNewsApiKey: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                bottom = baseSpacing,
                start = baseSpacing,
                end = baseSpacing
            )
    ) {
        SettingsGroupTitle(title = stringResource(R.string.news_api_service))
        Spacer(modifier = Modifier.height(baseSpacing))
        NewsApiKey(
            newsApiKey = newsApiKey,
            onNewsApiKeyChanged = onNewsApiKeyChanged,
            isSettingNewsApiKey = isSettingNewsApiKey,
            onGenerateApiKey = onGenerateApiKey,
            onSaveNewsApiKey = onSaveNewsApiKey
        )
    }
}

@Composable
private fun SettingsGroupTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = modifier)
}

@Composable
private fun SettingActionButton(
    modifier: Modifier = Modifier,
    isTakingAction: Boolean,
    label: String,
    onAction: () -> Unit,
) {
    Crossfade(
        modifier = modifier,
        targetState = isTakingAction
    ) { isTakingAction ->
        if (isTakingAction) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
        } else {
            TextButton(
                onClick = onAction
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SettingsError(
    modifier: Modifier = Modifier,
    error: UserSettingsScreenError?,
    isVisible: Boolean,
    onErrorHandled: () -> Unit
) {
    AnimatedVisibility(
        isVisible,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(baseSpacingDiv4))
        error?.let {
            Prompt(message = it.toMessage(), type = PromptType.Error)
            LaunchedEffect(Unit) {
                delay(timeMillis = 5000)
                onErrorHandled()
            }
        }
    }
}

@Composable
private fun SettingLabel(
    modifier: Modifier = Modifier,
    label: String
) {
    Text(label, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountrySelector(
    modifier: Modifier = Modifier,
    areCountriesUpdating: Boolean,
    countries: List<UiCountry>,
    selectedCountry: UiCountry?,
    error: UserSettingsScreenError?,
    onCountrySelected: (country: UiCountry) -> Unit,
    onErrorHandled: () -> Unit,
    onUpdateCountries: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedCountry?.name ?: "",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable
                    )
                    .fillMaxWidth(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country.name) },
                        onClick = {
                            onCountrySelected(country)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        SettingsError(
            error = error,
            isVisible = (error == UserSettingsScreenError.NoCountry),
            onErrorHandled = onErrorHandled
        )
        Spacer(modifier = Modifier.height(baseSpacingDiv2))
        SettingActionButton(
            isTakingAction = areCountriesUpdating,
            label = stringResource(R.string.update_countries_label).capitaliseWithLocal(),
            onAction = onUpdateCountries
        )
        SettingsError(
            error = error,
            isVisible = (error == UserSettingsScreenError.NetWork),
            onErrorHandled = onErrorHandled
        )
    }
}

@Composable
private fun LoadTrendingHeadlinesBySelector(
    modifier: Modifier = Modifier,
    loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
    onOptionSelected: (LoadTrendingHeadlinesBySelection) -> Unit
) {
    Column(modifier = modifier) {
        SettingLabel(
            label = stringResource(R.string.load_trending_headlines_by_label).capitaliseWithLocal()
        )
        Spacer(modifier = Modifier.height(baseSpacingDiv2))
        Column {
            LoadTrendingHeadlinesBySelection.entries.forEach { option ->
                val label = option.getLabel()
                val selected = (option == loadTrendingHeadlinesBy)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics {
                            role = Role.RadioButton
                            this.selected = selected
                        }
                        .clickable {
                            onOptionSelected(option)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = null // null for accessibility with screen readers
                    )
                    SettingLabel(
                        modifier = Modifier.padding(start = baseSpacing),
                        label = label
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsApiKey(
    modifier: Modifier = Modifier,
    isSettingNewsApiKey: Boolean,
    newsApiKey: String,
    onGenerateApiKey: () -> Unit,
    onNewsApiKeyChanged: (String) -> Unit,
    onSaveNewsApiKey: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        var hasInteraction by rememberSaveable { mutableStateOf(false) }
        var isShowKey by rememberSaveable { mutableStateOf(true) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newsApiKey,
            onValueChange = { value ->
                onNewsApiKeyChanged(value)
                hasInteraction = true
            },
            placeholder = {
                SettingLabel(
                    label = stringResource(
                        R.string.enter_your_api_key_prompt,
                        stringResource(R.string.news_api_service)
                    )
                        .capitaliseWithLocal()
                )
            },
            visualTransformation = if (isShowKey) PasswordVisualTransformation()
            else
                VisualTransformation.None,
            trailingIcon = {
                IconButton(
                    onClick = { isShowKey = !isShowKey }
                ) {
                    if (isShowKey) {
                        Icon(
                            painter = painterResource(R.drawable.outline_visibility_24),
                            contentDescription = stringResource(
                                R.string.show_news_api_key_content_description,
                                stringResource(R.string.news_api_service)
                            )
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.outline_visibility_off_24),
                            contentDescription = stringResource(
                                R.string.hide_news_api_key_content_description,
                                stringResource(R.string.news_api_service)
                            )
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(baseSpacingDiv2))
        AnimatedVisibility(newsApiKey.isNotBlank() && hasInteraction) {
            SettingActionButton(
                isTakingAction = isSettingNewsApiKey,
                label = stringResource(R.string.save_key_label).capitaliseWithLocal(),
                onAction = {
                    onSaveNewsApiKey(newsApiKey)
                    hasInteraction = false
                }
            )
        }
        Prompt(
            modifier = Modifier.clickable {
                onGenerateApiKey()
            },
            message = stringResource(
                R.string.generate_news_api_key_prompt,
                stringResource(R.string.news_api_service)
            ).capitaliseWithLocal(),
            type = PromptType.Link
        )
    }
}

@Composable
private fun LoadTrendingHeadlinesBySelection.getLabel() = when (this) {
    LoadTrendingHeadlinesBySelection.Country -> stringResource(R.string.country_label).capitaliseWithLocal()
    LoadTrendingHeadlinesBySelection.Sources -> stringResource(R.string.sources_label).capitaliseWithLocal()
}

@Preview
@Composable
fun UserSettingsScreenInitialisedPreview() {
    PNAMTheme {
        UserSettingsContent(
            state = UserSettingsScreenUiState.Initialised(
                areCountriesUpdating = false,
                countries = emptyList(),
                country = null,
                error = null,
                generateNewsApiUrl = "",
                isSettingNewsApiKey = false,
                languageCode = null,
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBySelection.Country,
                newsApiKey = "",
                sourcesIds = emptySet()
            ),
            onErrorHandled = {},
            onLoadData = {},
            onOpenExternalUrl = { _ -> Result.Success(Unit) },
            onNewsApiKeyChanged = { _ -> },
            onSetNewsApiKey = {},
            onSetLoadTrendingHeadlinesBy = { _ -> },
            onSetTrendingHeadlinesCountry = { _ -> },
            onUpdateCountries = {}
        )
    }
}

@Preview
@Composable
fun UserSettingsScreenNotInitialisedPreview() {
    PNAMTheme {
        UserSettingsContent(
            state = UserSettingsScreenUiState.NotInitialised,
            onErrorHandled = {},
            onLoadData = {},
            onOpenExternalUrl = { _ -> Result.Success(Unit) },
            onNewsApiKeyChanged = { _ -> },
            onSetNewsApiKey = {},
            onSetLoadTrendingHeadlinesBy = { _ -> },
            onSetTrendingHeadlinesCountry = { _ -> },
            onUpdateCountries = {}
        )
    }
}