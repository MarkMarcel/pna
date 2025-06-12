package com.marcel.pna.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.capitalize
import androidx.core.os.LocaleListCompat
import java.util.Locale

private val supportedLocales = listOf(
    Locale.ENGLISH,
    Locale.GERMAN,
)

@Composable
fun rememberDeviceLanguageCode(): String {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        val currentLocales = LocaleListCompat.create(
            *configuration.locales.toLanguageTags().split(",").map { Locale.forLanguageTag(it) }
                .toTypedArray()
        )
        val matchedLocale =
            currentLocales.getFirstMatch(supportedLocales.map { it.toLanguageTag() }.toTypedArray())
        (matchedLocale ?: Locale.ENGLISH).language
    }
}

fun String.capitaliseWithLocal(): String {
    return capitalize(
        androidx.compose.ui.text.intl.Locale.current
    )
}
