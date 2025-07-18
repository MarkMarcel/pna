package com.marcel.pna.ui.usersettings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.marcel.pna.R
import com.marcel.pna.core.capitaliseWithLocal

@Composable
fun UserSettingsScreenError.toMessage(): String {
    val message = when (this) {
        UserSettingsScreenError.NetWork -> stringResource(R.string.network_error)
        UserSettingsScreenError.NoCountry -> stringResource(R.string.select_trending_headlines_country_prompt).capitaliseWithLocal()
        UserSettingsScreenError.Unknown -> stringResource(R.string.unknown_error).capitaliseWithLocal()
        else -> ""
    }
    return message.capitaliseWithLocal()
}