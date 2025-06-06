package com.marcel.pna.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.marcel.pna.components.Centered
import com.marcel.pna.theme.PNAMTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
    onGoToUserSettings: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeScreenTopBar(
                navigationIcon = navigationIcon
            )
        }
    ) { paddingValues ->
        HomeScreenContent(
            modifier = Modifier.padding(paddingValues),
            onGoToUserSettings = onGoToUserSettings
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenTopBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit,
) {
    MediumTopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = navigationIcon,
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onGoToUserSettings: () -> Unit,
) {
    Centered(modifier) {
        Button(onClick = onGoToUserSettings) {
            Text(text = "Settings")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    PNAMTheme {
        HomeScreen(
            navigationIcon = {},
            onGoToUserSettings = {}
        )
    }
}