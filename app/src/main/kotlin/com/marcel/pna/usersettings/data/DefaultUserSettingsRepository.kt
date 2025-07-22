package com.marcel.pna.usersettings.data

import com.marcel.pna.core.Result
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DefaultUserSettingsRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: UserSettingsLocalDataSource
) : UserSettingsRepository {
    override fun getSettings(): Flow<UserSettings> {
        return localDataSource.getSettings().flowOn(ioDispatcher)
    }

    override suspend fun updateSettings(userSettingsUpdate: UserSettingsUpdate): Result<UserSettingsError, Unit> {
        return withContext(ioDispatcher) {
            localDataSource.updateSettings(userSettingsUpdate)
        }
    }
}