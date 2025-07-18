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
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare
import kotlin.test.assertEquals

class GetUserSettingsUseCaseTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            listOf(
                TestModule,
                UserSettingsTestModule,
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When called, Then return flow correct settings values`() =
        runTest {
            declareTestDispatchers(this)
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
            // collect emitted settings
            val emittedSettings = mutableListOf<UserSettings>()
            val collectionJob = launch {
                val useCase = GetUserSettingsUseCase(
                    backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
                    userSettingsRepository = get()
                )
                useCase.run().collect {
                    emittedSettings.add(it)
                }
            }
            // emit default settings
            fakeStore.tryEmit(preferencesOf())
            runCurrent()
            // Trigger settings update
            get<UserSettingsRepository>().updateSettings(validLoadTrendingHeadlinesBySources)
                .fold(
                    onFailure = { throw Exception("Update settings failed with error: $it") },
                    onSuccess = {},
                )
            runCurrent()
            collectionJob.cancel()
            assertEquals(2, emittedSettings.size)
            assertEquals(
                expected = emittedSettings.first()
                    .copy(loadTrendingHeadlinesBy = validLoadTrendingHeadlinesBySources.loadTrendingHeadlinesBy!!),
                actual = emittedSettings.last()
            )
        }

}