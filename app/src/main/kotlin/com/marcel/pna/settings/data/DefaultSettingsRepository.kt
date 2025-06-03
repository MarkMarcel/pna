package com.marcel.pna.settings.data

import com.marcel.pna.core.Result
import com.marcel.pna.settings.domain.Settings
import com.marcel.pna.settings.domain.SettingsError
import com.marcel.pna.settings.domain.SettingsRepository
import com.marcel.pna.settings.domain.SettingsUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DefaultSettingsRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {
    override fun getSettings(): Flow<Settings> {
        return localDataSource.getSettings().flowOn(ioDispatcher)
    }

    override suspend fun updateSettings(settingsUpdate: SettingsUpdate): Result<SettingsError, Unit> {
        return withContext(ioDispatcher) {
            localDataSource.updateSettings(settingsUpdate)
        }
    }
}