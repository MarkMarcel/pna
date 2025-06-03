package com.marcel.pna.settings.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateSettingsUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val settingsRepository: SettingsRepository
) {
    suspend fun invoke(settingsUpdate: SettingsUpdate): Result<SettingsError, Unit> {
        return withContext(backgroundDispatcher) {
            settingsRepository.updateSettings(settingsUpdate)
        }
    }
}