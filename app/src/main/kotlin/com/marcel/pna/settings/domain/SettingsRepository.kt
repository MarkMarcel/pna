package com.marcel.pna.settings.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun updateSettings(settingsUpdate: SettingsUpdate): Result<SettingsError, Unit>
}