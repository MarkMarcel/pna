package com.marcel.pna.settings.domain.usecases

import com.marcel.pna.core.Result
import com.marcel.pna.settings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.settings.domain.SettingsError
import com.marcel.pna.settings.domain.SettingsRepository
import com.marcel.pna.settings.domain.SettingsUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateSettingsUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val settingsRepository: SettingsRepository
) {
    suspend fun invoke(settingsUpdate: SettingsUpdate): Result<SettingsError, Unit> {
        return withContext(backgroundDispatcher) {
            if (
                settingsUpdate.loadTrendingHeadlinesBy == LoadTrendingHeadlinesBy.Sources &&
                settingsUpdate.headlinesSourcesIds.isNullOrEmpty()
            ) {
                Result.Failure(SettingsError.NoSources)
            }
            settingsRepository.updateSettings(settingsUpdate)
        }
    }
}