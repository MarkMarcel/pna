package com.marcel.pna.settings.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetSettingsUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> =
        settingsRepository.getSettings().flowOn(backgroundDispatcher)
}