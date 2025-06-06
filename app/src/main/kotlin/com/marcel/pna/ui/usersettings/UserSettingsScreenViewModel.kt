package com.marcel.pna.ui.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserSettingsScreenViewModel : ViewModel() {
    val uiState: StateFlow<UserSettingsScreenUiState>
        get() = modelState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = UserSettingsScreenUiState.NotInitialised
            )

    private val modelState =
        MutableStateFlow<UserSettingsScreenModelState>(UserSettingsScreenModelState.NotInitialised)

    fun onIntent(intent: UserSettingsIntent){

    }
}