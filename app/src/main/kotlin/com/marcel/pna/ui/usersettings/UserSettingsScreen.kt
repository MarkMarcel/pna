package com.marcel.pna.ui.usersettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcel.pna.R
import com.marcel.pna.components.Centered
import com.marcel.pna.components.Prompt
import com.marcel.pna.components.kotlinextensions.toTitleCase
import com.marcel.pna.components.theme.baseSpacingDiv2
import com.marcel.pna.core.capitaliseWithLocal
import com.marcel.pna.core.getDeviceLanguage
import com.marcel.pna.countries.domain.Country
import com.marcel.pna.theme.PNAMTheme
import com.marcel.pna.theme.baseSpacing
import com.marcel.pna.theme.baseSpacingDiv4
import com.marcel.pna.usersettings.domain.UserSettingsError
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserSettingsScreen(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
    viewModel: UserSettingsScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = {
            UserSettingsTopBar(
                navigationIcon = navigationIcon
            )
        }
    ) { contentPadding ->
        when (uiState) {
            is UserSettingsScreenUiState.NotInitialised -> {
                Centered(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
                LaunchedEffect(uiState) {
                    viewModel.onIntent(UserSettingsIntent.LoadSettings)
                }
            }

            is UserSettingsScreenUiState.Initialised -> {
                UserSettingsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = contentPadding.calculateTopPadding()),
                    state = uiState as UserSettingsScreenUiState.Initialised,
                    onSetLoadTrendingHeadlinesBy = { loadTrendingHeadlinesBy ->
                        viewModel.onIntent(
                            UserSettingsIntent.SetLoadTrendingHeadlinesBy(
                                loadTrendingHeadlinesBy
                            )
                        )
                    },
                    onSetTrendingHeadlinesCountry = { country ->
                        viewModel.onIntent(UserSettingsIntent.SetTrendingHeadlinesCountry(country = country))
                    }
                )
            }
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
    state: UserSettingsScreenUiState.Initialised,
    onSetLoadTrendingHeadlinesBy: (LoadTrendingHeadlinesBySelection) -> Unit,
    onSetTrendingHeadlinesCountry: (Country) -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            HeadlinesSettings(
                areCountriesUpdating = state.areCountriesUpdating,
                countries = state.countries,
                error = state.error,
                loadTrendingHeadlinesBy = state.loadTrendingHeadlinesBy,
                selectedCountry = state.country,
                onSetLoadTrendingHeadlinesBy = onSetLoadTrendingHeadlinesBy,
                onSetTrendingHeadlinesCountry = onSetTrendingHeadlinesCountry
            )
        }
    }
}

@Composable
private fun HeadlinesSettings(
    modifier: Modifier = Modifier,
    areCountriesUpdating: Boolean,
    countries: List<Country>,
    selectedCountry: Country?,
    error: UserSettingsError?,
    loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
    onSetLoadTrendingHeadlinesBy: (LoadTrendingHeadlinesBySelection) -> Unit,
    onSetTrendingHeadlinesCountry: (Country) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingsGroupTitle(title = stringResource(R.string.headlines_heading).toTitleCase())
        Spacer(modifier = Modifier.height(baseSpacing))
        LoadTrendingHeadlinesBySelector(
            modifier = Modifier.fillMaxWidth(),
            loadTrendingHeadlinesBy = loadTrendingHeadlinesBy,
            onOptionSelected = onSetLoadTrendingHeadlinesBy
        )
        AnimatedVisibility(loadTrendingHeadlinesBy == LoadTrendingHeadlinesBySelection.Country) {
            CountrySelector(
                areCountriesUpdating = areCountriesUpdating,
                countries = countries,
                selectedCountry = selectedCountry,
                error = error,
                onCountrySelected = onSetTrendingHeadlinesCountry
            )
        }
    }
}

@Composable
private fun SettingsGroupTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(title, style = MaterialTheme.typography.titleSmall, modifier = modifier)
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
    countries: List<Country>,
    selectedCountry: Country?,
    error: UserSettingsError?,
    onCountrySelected: (country: Country) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val language = getDeviceLanguage()
    val germanLanguageCode = "de"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCountry?.run { if (language == germanLanguageCode) germanName else englishName }
                        ?: "",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable
                        )
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country.run { if (language == germanLanguageCode) germanName else englishName }) },
                            onClick = {
                                onCountrySelected(country)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            AnimatedVisibility(areCountriesUpdating) {
                Row {
                    Spacer(modifier = Modifier.width(baseSpacing))
                    CircularProgressIndicator()
                }
            }

        }
        AnimatedVisibility(error == UserSettingsError.NoCountry) {
            Spacer(modifier = Modifier.height(baseSpacingDiv4))
            error?.let {
                Prompt(message = it.toMessage(), isError = true)
            }
        }
    }
}

@Composable
private fun LoadTrendingHeadlinesBySelector(
    modifier: Modifier = Modifier,
    loadTrendingHeadlinesBy: LoadTrendingHeadlinesBySelection,
    onOptionSelected: (LoadTrendingHeadlinesBySelection) -> Unit
) {

    Column(modifier = modifier.fillMaxWidth()) {
        SettingLabel(
            label = stringResource(R.string.load_trending_headlines_by_label).capitaliseWithLocal()
        )
        Spacer(modifier = Modifier.height(baseSpacingDiv2))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = baseSpacing)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadTrendingHeadlinesBySelection.getLabel() = when (this) {
    LoadTrendingHeadlinesBySelection.Country -> "country".capitaliseWithLocal()
    LoadTrendingHeadlinesBySelection.Sources -> "sources".capitaliseWithLocal()
}

@Preview
@Composable
fun UserSettingsScreenPreview() {
    PNAMTheme {
        UserSettingsScreen(
            navigationIcon = {}
        )
    }
}