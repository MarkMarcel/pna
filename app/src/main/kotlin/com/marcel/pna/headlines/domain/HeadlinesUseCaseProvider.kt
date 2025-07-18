package com.marcel.pna.headlines.domain

import kotlinx.coroutines.CoroutineDispatcher

class HeadlinesUseCaseProvider(
    backgroundDispatcher: CoroutineDispatcher,
    headlinesRepository: HeadlinesRepository
) {
    val getHeadlinesUseCase = GetHeadlinesUseCase(
        backgroundDispatcher = backgroundDispatcher,
        headlinesRepository = headlinesRepository,
    )
}