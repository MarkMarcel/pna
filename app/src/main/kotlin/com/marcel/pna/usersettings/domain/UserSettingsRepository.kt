package com.marcel.pna.usersettings.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateSettings(userSettingsUpdate: UserSettingsUpdate): Result<UserSettingsError, Unit>
}