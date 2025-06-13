package com.marcel.pna.headlines.data

import com.marcel.pna.AppConfig
import com.marcel.pna.core.Logger
import com.marcel.pna.core.Result
import com.marcel.pna.headlines.data.models.NewsApiResponse
import com.marcel.pna.headlines.data.models.NewsErrorApiResponse
import com.marcel.pna.headlines.data.models.getLocalIdForArticle
import com.marcel.pna.headlines.data.models.toHeadlinesPage
import com.marcel.pna.headlines.domain.HeadlinesLoadError
import com.marcel.pna.headlines.domain.HeadlinesPage
import com.marcel.pna.headlines.domain.HeadlinesRequest
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.squareup.moshi.Moshi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException

class HeadlinesRemoteDataSource(
    private val appConfigProvider: () -> AppConfig,
    private val api: HeadlinesApi,
    private val logger: Logger,
    private val moshi: Moshi
) {
    private var previousRequest: HeadlinesRequest.Trending? = null

    // Mutex to synchronize access to previousRequest from multiple coroutines
    private val mutex = Mutex()

    suspend fun getTrendingHeadlines(
        apiKey: String,
        request: HeadlinesRequest.Trending
    ): Result<HeadlinesLoadError, HeadlinesPage> {
        mutex.withLock {
            if (request == previousRequest) {
                return Result.Failure(HeadlinesLoadError.Debounced)
            }
            previousRequest = request
        }
        // Invalid request isn't an error but an exception, that's why it is here
        if (!request.isValid) {
            throw IllegalArgumentException("Invalid HeadlinesRequest.Trending, ensure values match LoadTrendingHeadlinesBy")
        }

        val result = Result.catching(
            delayMillisProvider = { attempt -> attempt * 1000L },
            shouldRetryProvider = { attempt, error -> attempt <= 3 && error is IOException }
        ) {
            val pageSize = request.pageSize
                ?: appConfigProvider().headlinesConfig.headlinesPerRequest
            when (request.loadTrendingHeadlinesBy) {
                is LoadTrendingHeadlinesBy.Country -> {
                    val response: NewsApiResponse = api.getTrendingHeadlinesFromCountry(
                        apiKey = apiKey,
                        country = request.loadTrendingHeadlinesBy.alpha2Code,
                        page = request.page,
                        pageSize = pageSize
                    )
                    response.toHeadlinesPage(
                        currentPage = request.page,
                        pageSize = pageSize,
                        getLocalIdForArticle = ::getLocalIdForArticle
                    )
                }

                is LoadTrendingHeadlinesBy.Sources -> {
                    HeadlinesPage(headlines = emptyList(), nextPage = null)
                }
            }
        }.mapFailure { throwable ->
            logger.logError(throwable)
            when (throwable) {
                is IOException -> HeadlinesLoadError.Network
                is HttpException -> {
                    val errorBody = throwable.response()?.errorBody()
                    val adapter = moshi.adapter(NewsErrorApiResponse::class.java)
                    val error = errorBody?.let { adapter.fromJson(it.source()) }

                    when (error?.code) {
                        "apiKeyDisabled" -> HeadlinesLoadError.ApiKeyDisabled
                        "apiKeyExhausted" -> HeadlinesLoadError.ApiKeyExhausted
                        "apiKeyInvalid" -> HeadlinesLoadError.ApiKeyInvalid
                        "apiKeyMissing" -> HeadlinesLoadError.ApiKeyInvalid
                        "parameterInvalid",
                        "parametersMissing" -> HeadlinesLoadError.Server

                        "rateLimited" -> HeadlinesLoadError.RateLimited
                        "sourcesTooMany" -> HeadlinesLoadError.SourcesTooMany
                        "sourceDoesNotExist" -> HeadlinesLoadError.SourceDoesNotExist
                        "unexpectedError" -> HeadlinesLoadError.Server
                        else -> HeadlinesLoadError.Server
                    }
                }

                else -> HeadlinesLoadError.Server
            }
        }
        // Reset previousRequest on failure to allow a new request
        if (result is Result.Failure) {
            mutex.withLock {
                previousRequest = null
            }
        }
        return result
    }
}