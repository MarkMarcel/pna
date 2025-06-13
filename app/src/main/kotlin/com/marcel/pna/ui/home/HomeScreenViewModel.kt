package com.marcel.pna.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeScreenViewModel : ViewModel() {
    val uiState: StateFlow<HomeScreenUiState> by lazy {
        modelState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = HomeScreenModelState().toUiState()
            )
    }
    private val modelState = MutableStateFlow<HomeScreenModelState>(HomeScreenModelState())

    fun onIntent(intent: HomeScreenIntent) {
        when (intent) {
            is HomeScreenIntent.SelectedHeadlineCategoryChanged -> {
                modelState.update { state ->
                    state.copy(selectedCategoryIndex = intent.index)
                }
            }
        }
    }
}