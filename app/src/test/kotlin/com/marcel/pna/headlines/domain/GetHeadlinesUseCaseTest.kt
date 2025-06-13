package com.marcel.pna.headlines.domain

import com.marcel.pna.TestModule
import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.Result
import com.marcel.pna.countries.countriesTestData
import com.marcel.pna.declareTestDispatchers
import com.marcel.pna.headlines.apiKeyDisabledErrorBody
import com.marcel.pna.headlines.apiKeyExhaustedErrorBody
import com.marcel.pna.headlines.apiKeyInvalidErrorBody
import com.marcel.pna.headlines.apiKeyMissingErrorBody
import com.marcel.pna.headlines.data.DefaultHeadlinesRepository
import com.marcel.pna.headlines.data.HeadlinesApi
import com.marcel.pna.headlines.data.HeadlinesRemoteDataSource
import com.marcel.pna.headlines.data.models.NewsApiResponse
import com.marcel.pna.headlines.data.models.getLocalIdForArticle
import com.marcel.pna.headlines.data.models.toHeadlinesPage
import com.marcel.pna.headlines.headlinesResponseTestData
import com.marcel.pna.headlines.parameterInvalidErrorBody
import com.marcel.pna.headlines.parametersMissingErrorBody
import com.marcel.pna.headlines.rateLimitedErrorBody
import com.marcel.pna.headlines.sourceDoesNotExistErrorBody
import com.marcel.pna.headlines.sourcesTooManyErrorBody
import com.marcel.pna.headlines.unexpectedErrorBody
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.userSettingsDefaultTestInstance
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetHeadlinesUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            listOf(
                TestModule,
                module {
                    single<Moshi> {
                        Moshi.Builder().build()
                    }
                    single { mockk<HeadlinesApi>() }
                    single {
                        HeadlinesRemoteDataSource(
                            appConfigProvider = get(),
                            api = get(),
                            logger = get(),
                            moshi = get()
                        )
                    }
                    single<HeadlinesRepository> {
                        DefaultHeadlinesRepository(
                            ioDispatcher = get(named(IO_DISPATCHER)),
                            remoteDataSource = get(),
                            userSettingsRepository = get()
                        )
                    }
                    single {
                        mockk<UserSettingsRepository> {
                            every { getSettings() } returns flowOf(userSettingsDefaultTestInstance)
                        }
                    }
                }
            ),
        )
    }

    @Test
    fun `When HeadlinesApi fails, Then corresponding error returned`() = runTest {
        declareTestDispatchers(this)
        val errors = listOf(
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    apiKeyDisabledErrorBody
                )
            ) to HeadlinesLoadError.ApiKeyDisabled,
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    apiKeyExhaustedErrorBody
                )
            ) to HeadlinesLoadError.ApiKeyExhausted,
            HttpException(
                Response.error<NewsApiResponse>(
                    401,
                    apiKeyInvalidErrorBody
                )
            ) to HeadlinesLoadError.ApiKeyInvalid,
            HttpException(
                Response.error<NewsApiResponse>(
                    401,
                    apiKeyMissingErrorBody
                )
            ) to HeadlinesLoadError.ApiKeyInvalid,
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    parameterInvalidErrorBody
                )
            ) to HeadlinesLoadError.Server,
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    parametersMissingErrorBody
                )
            ) to HeadlinesLoadError.Server,
            HttpException(
                Response.error<NewsApiResponse>(
                    429,
                    rateLimitedErrorBody
                )
            ) to HeadlinesLoadError.RateLimited,
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    sourcesTooManyErrorBody
                )
            ) to HeadlinesLoadError.SourcesTooMany,
            HttpException(
                Response.error<NewsApiResponse>(
                    400,
                    sourceDoesNotExistErrorBody
                )
            ) to HeadlinesLoadError.SourceDoesNotExist,
            HttpException(
                Response.error<NewsApiResponse>(
                    500,
                    unexpectedErrorBody
                )
            ) to HeadlinesLoadError.Server,
            IOException("Network issue") to HeadlinesLoadError.Network // Must be last due to retries in datasource
        )
        var request = HeadlinesRequest.Trending(
            loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
                alpha2Code = countriesTestData.first().alpha2Code
            )
        )

        // Mock TrendingHeadlinesApi to throw errors and override dependency
        val headlinesApiMock = mockk<HeadlinesApi> {
            coEvery {
                getTrendingHeadlinesFromCountry(
                    apiKey = any(),
                    country = any(),
                    page = any(),
                    pageSize = any()
                )
            } throwsMany errors.map { error -> error.first }
        }
        declare<HeadlinesApi> { headlinesApiMock }

        val useCase = GetHeadlinesUseCase(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            headlinesRepository = get(),
        )
        for ((exception, domainError) in errors) {
            val result = useCase.run(request)
            assertTrue { result is Result.Failure }
            assertEquals(
                expected = Result.Failure(domainError),
                actual = result,
                message = "Expected error $domainError for exception: ${exception::class.simpleName}"
            )
            coVerify(atLeast = 1) {
                headlinesApiMock.getTrendingHeadlinesFromCountry(
                    apiKey = any(),
                    country = any(),
                    page = any(),
                    pageSize = any()
                )
            }
        }
        confirmVerified(headlinesApiMock)
        // increase page to avoid debouncing
        request = request.run {
            copy(page = page + 1)
        }
    }

    @Test
    fun `When HeadlinesApi succeeds, Then data from api returned`() = runTest {
        declareTestDispatchers(this)
        val headlinesTestData = headlinesResponseTestData.articles
        if (headlinesTestData.size < 2) throw Exception("Response must have at leas 2 articles")
        val pageSize = (headlinesTestData.size / 2 + 1)
        val page1Articles =
            headlinesTestData.subList(fromIndex = 0, toIndex = pageSize)
        val page2Articles =
            headlinesTestData.subList(
                fromIndex = (headlinesTestData.size / 2 + 1),
                toIndex = headlinesTestData.size
            )
        val responses = listOf(
            headlinesResponseTestData.copy(articles = page1Articles),
            headlinesResponseTestData.copy(articles = page2Articles),
        )
        var request = HeadlinesRequest.Trending(
            loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
                alpha2Code = countriesTestData.first().alpha2Code
            ),
            pageSize = pageSize
        )

        // Mock TrendingHeadlinesApi to return data for page
        val headlinesApiMock = mockk<HeadlinesApi> {
            coEvery {
                getTrendingHeadlinesFromCountry(
                    apiKey = any(),
                    country = any(),
                    page = any(),
                    pageSize = any()
                )
            } returnsMany (responses)
        }
        declare<HeadlinesApi> { headlinesApiMock }

        // Mock Id generation
        mockkStatic(::getLocalIdForArticle)
        every { getLocalIdForArticle() } returns "fake-uuid"

        val useCase = GetHeadlinesUseCase(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            headlinesRepository = get(),
        )
        for (i in (1..2)) {
            val currentRequest = request.copy(page = i)
            val result = useCase.run(request = currentRequest)
            assert(result is Result.Success)
            coVerify(atLeast = 1) {
                headlinesApiMock.getTrendingHeadlinesFromCountry(
                    apiKey = any(),
                    country = any(),
                    page = any(),
                    pageSize = any()
                )
            }
            val expectedData = responses[i - 1].toHeadlinesPage(
                currentPage = currentRequest.page,
                pageSize = pageSize,
                getLocalIdForArticle = { getLocalIdForArticle() }
            )
            assertEquals(
                expected = expectedData,
                actual = (result as Result.Success).value
            )
        }
        confirmVerified(headlinesApiMock)
    }

    @Test
    fun `When invalid HeadlinesRequest received, Then exception thrown`() = runTest {
        declareTestDispatchers(this)
        val invalidRequest = HeadlinesRequest.Trending(
            loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(alpha2Code = ""),
        )
        val useCase = GetHeadlinesUseCase(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            headlinesRepository = get(),
        )

        assertFailsWith<IllegalArgumentException> { useCase.run(invalidRequest) }
    }

    @Test
    fun `When same HeadlinesRequest received in succession, Then request is debounced`() = runTest {
        declareTestDispatchers(this)
        val request = HeadlinesRequest.Trending(
            loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(
                alpha2Code = countriesTestData.first().alpha2Code
            )
        )

        // Mock TrendingHeadlinesApi and override dependency
        declare {
            mockk<HeadlinesApi> {
                coEvery {
                    getTrendingHeadlinesFromCountry(
                        apiKey = any(),
                        country = any(),
                        page = any(),
                        pageSize = any()
                    )
                } returns headlinesResponseTestData
            }
        }

        val useCase = GetHeadlinesUseCase(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            headlinesRepository = get(),
        )
        // First call should return success
        assert(useCase.run(request = request) is Result.Success)
        // Second call should return error
        val result = useCase.run(request = request)
        assert(result is Result.Failure)
        assert((result as Result.Failure).reason == HeadlinesLoadError.Debounced)
    }


}