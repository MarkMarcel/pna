package com.marcel.pna.ui

import com.marcel.pna.ui.usersettings.UserSettingsScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
    viewModelOf(::UserSettingsScreenViewModel)
}