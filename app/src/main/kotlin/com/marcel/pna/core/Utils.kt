package com.marcel.pna.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.capitalize
import java.util.Locale

@Composable
fun getDeviceLanguage(): String {
    val configuration = LocalConfiguration.current
    val currentLocale: Locale = configuration.locales[0] // Get the primary locale
    return currentLocale.language
}

fun String.capitaliseWithLocal(): String {
    return capitalize(
        androidx.compose.ui.text.intl.Locale.current
    )
}