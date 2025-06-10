package com.marcel.pna.countries.domain.usecases

import com.marcel.pna.countries.countryApiResponsesTestData
import com.marcel.pna.countries.countryDatabaseModelsTestData
import com.marcel.pna.countries.data.CountriesLocalDataSource
import com.marcel.pna.countries.data.CountriesRemoteDataSource
import com.marcel.pna.countries.data.CountriesRoomDao
import com.marcel.pna.countries.data.DefaultCountriesRepository
import com.marcel.pna.countries.data.RestCountriesApi
import com.marcel.pna.countries.data.models.toDomain
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.declareTestDispatchers
import com.marcel.pna.testLogger
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare
import kotlin.test.assertEquals

class GetCountriesUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.Companion.create {
        printLogger(level = Level.DEBUG)
        modules(
            module {
                single { testLogger }
                single {
                    mockk<RestCountriesApi>() {
                        coEvery { getCountries() } returns countryApiResponsesTestData
                    }
                }
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
    fun `Get countries returns correct data`() = runTest {
        val fakeDB = flowOf(countryDatabaseModelsTestData)
        // Mock data apis
        val countriesRoomDaoMock = mockk<CountriesRoomDao>(relaxed = true) {
            coEvery { getCountries() } returns fakeDB
        }
        declare<CountriesRoomDao> { countriesRoomDaoMock }

        // Replace coroutine dispatchers
        declareTestDispatchers(this) // `this` refers to runTest scope

        val useCase = GetCountriesUseCase(get(), get())
        val countries = useCase.run().firstOrNull()
        runCurrent()
        assertEquals(
            expected = countryApiResponsesTestData.map { it.toDomain() },
            actual = countries
        )
    }

}