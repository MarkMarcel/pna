package com.marcel.pna.usersettings.domain.usecases

import com.marcel.pna.core.Result
import com.marcel.pna.usersettings.domain.UserSettingsError
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateUserSettingsUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend fun invoke(userSettingsUpdate: UserSettingsUpdate): Result<UserSettingsError, Unit> {
        return withContext(backgroundDispatcher) {
            // Return with error from validation if it exist else continue to update
            userSettingsUpdate.getError()?.let {
                Result.Failure(it)
            } ?: userSettingsRepository.updateSettings(userSettingsUpdate)
        }
    }
}