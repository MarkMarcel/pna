package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.TestModule
import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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
import kotlin.test.assertTrue

class UpdateCountriesUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            listOf(
                TestModule,
                module {
                    single { CountriesLocalDataSource(countriesRoomDao = get(), logger = get()) }
                    single { CountriesRemoteDataSource(api = get(), logger = get()) }
                    single<CountriesRepository> {
                        DefaultCountriesRepository(
                            ioDispatcher = get(named(IO_DISPATCHER)),
                            localDataSource = get(),
                            remoteDataSource = get()
                        )
                    }
                }
            )
        )
    }

    @Test
    fun `When UpdateCountriesUseCase invoked then countries are updated`() = runTest {
        // Replace coroutine dispatchers
        declareTestDispatchers(this) // `this` refers to runTest scope

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
        val useCase = UpdateCountriesUseCase(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            countriesRepository = get()
        )
        useCase.run()
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

    @Test
    fun `When RestCountriesApi throws error then UpdateCountriesUseCase maps to correct CountryError`() =
        runTest {
            declareTestDispatchers(this)
            // Setup errors
            val errorBody = "Internal Server Error"
                .toResponseBody("application/json".toMediaType())
            val response = Response.error<List<RestCountryApiResponse>>(500, errorBody)
            val ioException = IOException("Network issue")
            val httpException = HttpException(response)
            val errors = listOf(
                httpException to CountryError.Server,
                ioException to CountryError.Network // Must be last due to retries in datasource
            )

            // Mock data apis. CountriesApi mocked such that it throws the exceptions in [errors] on
            // subsequent calls in order
            val restCountriesApiMock = mockk<RestCountriesApi> {
                coEvery { getCountries() } throwsMany errors.map { error -> error.first }
            }
            val countriesRoomDaoMock = mockk<CountriesRoomDao>(relaxed = true)
            declare<RestCountriesApi> { restCountriesApiMock }
            declare<CountriesRoomDao> { countriesRoomDaoMock }

            val useCase = UpdateCountriesUseCase(
                backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
                countriesRepository = get()
            )
            for ((exception, domainError) in errors) {
                val result = useCase.run()
                assertTrue { result is Result.Failure }
                assertEquals(
                    expected = Result.Failure(domainError),
                    actual = result,
                    message = "Expected error $domainError for exception: ${exception::class.simpleName}"
                )
                coVerify(atLeast = 1) { restCountriesApiMock.getCountries() }
            }
            confirmVerified(restCountriesApiMock)
        }
}