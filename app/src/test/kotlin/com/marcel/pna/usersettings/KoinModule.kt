package com.marcel.pna.usersettings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.usersettings.data.DefaultUserSettingsRepository
import com.marcel.pna.usersettings.data.UserSettingsLocalDataSource
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val UserSettingsTestModule = module {
    val dataStore = mockk<DataStore<Preferences>> {
        every { data } returns flowOf(preferencesOf())
    }
    single<DataStore<Preferences>>(named(SETTINGS_PREFERENCE_DATA_STORE)) { dataStore }
    single {
        UserSettingsLocalDataSource(
            appConfigProvider = get(),
            settingsDataStore = get(named(SETTINGS_PREFERENCE_DATA_STORE)),
            logger = get()
        )
    }
    single<UserSettingsRepository> {
        DefaultUserSettingsRepository(
            ioDispatcher = get(named(IO_DISPATCHER)),
            localDataSource = get()
        )
    }
}