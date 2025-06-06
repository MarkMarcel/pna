package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.core.Result
import com.marcel.pna.countries.countriesTestData
import com.marcel.pna.countries.countryApiResponsesTestData
import com.marcel.pna.countries.data.CountriesLocalDataSource
import com.marcel.pna.countries.data.CountriesRemoteDataSource
import com.marcel.pna.countries.data.CountriesRoomDao
import com.marcel.pna.countries.data.DefaultCountriesRepository
import com.marcel.pna.countries.data.RestCountriesApi
import com.marcel.pna.countries.data.models.CountryDatabaseModel
import com.marcel.pna.countries.data.models.RestCountryApiResponse
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.CountryError
import com.marcel.pna.declareTestDispatchers
import com.marcel.pna.testLogger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateCountriesUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.Companion.create {
        printLogger(level = Level.DEBUG)
        modules(
            module {
                single { testLogger }
                single { CountriesLocalDataSource(countriesRoomDao = get(), logger = get()) }
                single { CountriesRemoteDataSource(api = get(), logger = get()) }
                single<CountriesRepository> {
                    DefaultCountriesRepository(
                        ioDispatcher = get(),
                        localDataSource = get(),
                        remoteDataSource = get()
                    )
                }
            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When UpdateCountriesUseCase invoked then countries are updated`() = runTest {
        val fakeDB = Channel<List<CountryDatabaseModel>>(capacity = 1)
        // Mock data apis
        val countriesRoomDaoMock = mockk<CountriesRoomDao>(relaxed = true) {
            coEvery { getCountries() } returns fakeDB.receiveAsFlow()
            coEvery { insertCountries(any()) } answers {
                fakeDB.trySend(arg(0))
            }
        }
        val restCountriesApiMock = mockk<RestCountriesApi>() {
            coEvery { getCountries() } returns countryApiResponsesTestData
        }
        declare<CountriesRoomDao> { countriesRoomDaoMock }
        declare<RestCountriesApi> { restCountriesApiMock }

        // Replace coroutine dispatchers
        declareTestDispatchers(this) // `this` refers to runTest scope
        val useCase = UpdateCountriesUseCase(get(), get())
        useCase.invoke()
        runCurrent()
        val countries = get<CountriesRepository>().getCountries().firstOrNull()
        assertEquals(
            expected = countriesTestData,
            actual = countries
        )
        coVerifySequence {
            restCountriesApiMock.getCountries()
            countriesRoomDaoMock.insertCountries(any())
            countriesRoomDaoMock.getCountries()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When RestCountriesApi throws error then UpdateCountriesUseCase maps to correct CountryError`() =
        runTest {
            declareTestDispatchers(this)
            // Setup error
            val errorBody = "Internal Server Error"
                .toResponseBody("application/json".toMediaType())
            val response = Response.error<List<RestCountryApiResponse>>(500, errorBody)
            val httpError = HttpException(response)

            // Mock data apis
            val restCountriesApiMock = mockk<RestCountriesApi> {
                coEvery { getCountries() } throws httpError
            }
            val countriesRoomDaoMock = mockk<CountriesRoomDao>(relaxed = true)
            declare<RestCountriesApi> { restCountriesApiMock }
            declare<CountriesRoomDao> { countriesRoomDaoMock }

            val useCase = UpdateCountriesUseCase(get(), get())
            val result = useCase.invoke()
            assertTrue { result is Result.Failure }
            assertEquals(
                expected = Result.Failure(CountryError.Server),
                actual = result,
                message = "Expected error $httpError for exception: ${httpError::class.simpleName}"
            )
            coVerify(exactly = 1) { restCountriesApiMock.getCountries() }
            confirmVerified(restCountriesApiMock)
        }

}