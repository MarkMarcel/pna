package com.marcel.pna.headlines.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// Todo: test
class GetHeadlinesUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val headlinesRepository: HeadlinesRepository,
) {
    suspend fun run(request: HeadlinesRequest): Result<HeadlinesLoadError, HeadlinesPage> {
        return withContext(backgroundDispatcher) {
            headlinesRepository.getHeadlines(request)
        }
    }
}