package com.marcel.pna.usersettings.domain.usecases

import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetUserSettingsUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val userSettingsRepository: UserSettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> =
        userSettingsRepository.getSettings().flowOn(backgroundDispatcher)
}