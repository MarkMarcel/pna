package com.marcel.pna.usersettings.domain.usecases

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import com.marcel.pna.TestModule
import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.Result
import com.marcel.pna.declareTestDispatchers
import com.marcel.pna.usersettings.SETTINGS_PREFERENCE_DATA_STORE
import com.marcel.pna.usersettings.UserSettingsTestModule
import com.marcel.pna.usersettings.data.UserSettingsLocalDataSource
import com.marcel.pna.usersettings.data.UserSettingsLocalDataSource.Companion.setValues
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import com.marcel.pna.usersettings.domain.defaultCountryAlpha2Code
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateUserSettingsUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.Companion.create {
        printLogger()
        modules(
            listOf(
                TestModule,
                UserSettingsTestModule,
            )
        )
    }

    @Test
    fun `Given LoadTrendingHeadlinesBy Country, When alpha2Code is blank, Then return NoCountry error`() =
        runTest {
            // Replace coroutine dispatchers
            declareTestDispatchers(this) // `this` refers to runTest scope

            val invalidUpdate = UserSettingsUpdate(
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(alpha2Code = "")
            )
            val useCase = UpdateUserSettingsUseCase(
                backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
                userSettingsRepository = get()
            )
            val result = useCase.run(invalidUpdate)
            assertTrue { result is Result.Failure }
            assertEquals(
                expected = Result.Failure(UserSettingsError.NoCountry),
                actual = result
            )
        }

    @Test
    fun `Given LoadTrendingHeadlinesBy Sources, When sourceIds is empty, Then return NoSources error`() =
        runTest {
            declareTestDispatchers(this)

            val invalidUpdate = UserSettingsUpdate(
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Sources(sourceIds = emptySet())
            )
            val useCase = UpdateUserSettingsUseCase(
                backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
                userSettingsRepository = get()
            )
            val result = useCase.run(invalidUpdate)
            assertTrue { result is Result.Failure }
            assertEquals(
                expected = Result.Failure(UserSettingsError.NoSources),
                actual = result
            )
        }

    @Test
    fun `Given valid LoadTrendingHeadlinesBy update, When run, Then settings are updated`() =
        runTest {
            declareTestDispatchers(this)
            val validLoadTrendingHeadlinesByCountry = UserSettingsUpdate(
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(alpha2Code = "gh")
            )
            val validLoadTrendingHeadlinesBySources = UserSettingsUpdate(
                loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Sources(
                    sourceIds = setOf(
                        "abc",
                        "def"
                    )
                )
            )

            // Mock data apis
            val fakeStore = MutableSharedFlow<Preferences>(replay = 1)
            fakeStore.tryEmit(preferencesOf())
            val dataStore = mockk<DataStore<Preferences>> {
                every { data } returns fakeStore.asSharedFlow()
            }
            declare<DataStore<Preferences>>(named(SETTINGS_PREFERENCE_DATA_STORE)) { dataStore }
            val updatedDataSourceMock = spyk(get<UserSettingsLocalDataSource>()) {
                coEvery { updateSettings(any()) } answers {
                    val update = arg<UserSettingsUpdate>(0)
                    val updatedSettings = mutablePreferencesOf().apply {
                        setValues(update)
                    }
                    fakeStore.tryEmit(updatedSettings)
                    Result.Success(Unit)
                }
            }
            declare<UserSettingsLocalDataSource> { updatedDataSourceMock }

            // Get repo to confirm data changes
            val repo = get<UserSettingsRepository>()
            // Confirm default settings
            val defaultSettings = repo.getSettings().firstOrNull()
            assertTrue {
                defaultSettings?.loadTrendingHeadlinesBy is LoadTrendingHeadlinesBy.Country
            }
            assertEquals(
                expected = defaultCountryAlpha2Code,
                actual = (
                        defaultSettings?.loadTrendingHeadlinesBy as LoadTrendingHeadlinesBy.Country
                        )
                    .alpha2Code
            )

            val validUpdates = listOf(
                validLoadTrendingHeadlinesByCountry,
                validLoadTrendingHeadlinesBySources
            )
            val useCase = UpdateUserSettingsUseCase(
                backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
                userSettingsRepository = get()
            )
            for (update in validUpdates) {
                val result = useCase.run(update)
                assertTrue { result is Result.Success }
                val settings = repo.getSettings().first()
                assertEqualLoadTrendingHeadlinesBy(
                    updateRequest = update,
                    updatedSettings = settings
                )
            }
        }

    private fun assertEqualLoadTrendingHeadlinesBy(
        updateRequest: UserSettingsUpdate,
        updatedSettings: UserSettings
    ) {
        when (updateRequest.loadTrendingHeadlinesBy) {
            is LoadTrendingHeadlinesBy.Country -> {
                assertTrue {
                    updatedSettings.loadTrendingHeadlinesBy is LoadTrendingHeadlinesBy.Country
                }
                assertEquals(
                    expected = updateRequest.loadTrendingHeadlinesBy.alpha2Code,
                    actual = (
                            updatedSettings.loadTrendingHeadlinesBy as LoadTrendingHeadlinesBy.Country
                            )
                        .alpha2Code
                )

            }

            is LoadTrendingHeadlinesBy.Sources -> {
                assertTrue {
                    updatedSettings.loadTrendingHeadlinesBy is LoadTrendingHeadlinesBy.Sources
                }
                assertEquals(
                    expected = updateRequest.loadTrendingHeadlinesBy.sourceIds,
                    actual = (
                            updatedSettings.loadTrendingHeadlinesBy as LoadTrendingHeadlinesBy.Sources
                            )
                        .sourceIds
                )
            }

            null -> {}
        }
    }
}