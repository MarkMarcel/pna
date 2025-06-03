package com.marcel.pna.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.settings.data.DefaultSettingsRepository
import com.marcel.pna.settings.data.SettingsLocalDataSource
import com.marcel.pna.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val SETTINGS_PREFERENCE_DATA_STORE = "settings_preference_data_store"

private val Context.dataStore by preferencesDataStore(name = "pna_m_settings")

val SettingsModule = module {
    single(named(SETTINGS_PREFERENCE_DATA_STORE)) {
        androidContext().dataStore
    }
    single {
        SettingsLocalDataSource(
            appConfigProvider = get(),
            settingsDataStore = get(named(SETTINGS_PREFERENCE_DATA_STORE)),
            logger = get()
        )
    }
    single<SettingsRepository> {
        DefaultSettingsRepository(
            ioDispatcher = get(named(IO_DISPATCHER)),
            localDataSource = get()
        )

    }
}