package com.marcel.pna.ui.usersettings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.marcel.pna.R
import com.marcel.pna.core.capitaliseWithLocal
import com.marcel.pna.usersettings.domain.UserSettingsError

@Composable
fun UserSettingsError.toMessage(): String {
    val message = when (this) {
        UserSettingsError.NoCountry -> stringResource(R.string.select_trending_headlines_country_prompt)
        else -> ""
    }
    return message.capitaliseWithLocal()
}