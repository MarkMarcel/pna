package com.marcel.pna.headlines.domain

import com.marcel.pna.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// Todo: test
class GetHeadlinesUseCase(
    private val backgroundDispatcher: CoroutineDispatcher,
    private val headlinesRepository: HeadlinesRepository,
) {
    suspend operator fun invoke(request: HeadlinesRequest): Result<HeadlinesLoadError, HeadlinesPage> {
        return withContext(backgroundDispatcher) {
            headlinesRepository.getHeadlines(request)
        }
    }
}