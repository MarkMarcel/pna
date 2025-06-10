package com.marcel.pna.usersettings.domain.usecases

import com.marcel.pna.usersettings.domain.UserSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher

class UserSettingsUseCaseProvider(
    backgroundDispatcher: CoroutineDispatcher,
    userSettingsRepository: UserSettingsRepository,
) {
    val getUserSettingsUseCase = GetUserSettingsUseCase(
        backgroundDispatcher = backgroundDispatcher,
        userSettingsRepository = userSettingsRepository,
    )

    val updateUserSettingsUseCase = UpdateUserSettingsUseCase(
        backgroundDispatcher = backgroundDispatcher,
        userSettingsRepository = userSettingsRepository,
    )
}