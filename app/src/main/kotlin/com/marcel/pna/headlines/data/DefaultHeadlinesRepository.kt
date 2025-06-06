package com.marcel.pna.headlines.data

import com.marcel.pna.core.Result
import com.marcel.pna.headlines.domain.HeadlinesLoadError
import com.marcel.pna.headlines.domain.HeadlinesPage
import com.marcel.pna.headlines.domain.HeadlinesRepository
import com.marcel.pna.headlines.domain.HeadlinesRequest
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DefaultHeadlinesRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val remoteDataSource: HeadlinesRemoteDataSource,
    private val userSettingsRepository: UserSettingsRepository,
) : HeadlinesRepository {
    override suspend fun getHeadlines(request: HeadlinesRequest): Result<HeadlinesLoadError, HeadlinesPage> {
        return withContext(ioDispatcher) {
            val apiKey = userSettingsRepository.getSettings().first().apiKey
            when (request) {
                is HeadlinesRequest.Category -> Result.Success(HeadlinesPage(emptyList(), null))
                is HeadlinesRequest.Trending -> remoteDataSource.getTrendingHeadlines(
                    apiKey = apiKey,
                    request = request,
                )
            }
        }
    }

}