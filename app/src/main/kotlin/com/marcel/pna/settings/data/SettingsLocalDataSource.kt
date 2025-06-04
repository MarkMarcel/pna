package com.marcel.pna.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.marcel.pna.AppConfig
import com.marcel.pna.core.Logger
import com.marcel.pna.core.Result
import com.marcel.pna.settings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.settings.domain.Settings
import com.marcel.pna.settings.domain.SettingsError
import com.marcel.pna.settings.domain.SettingsUpdate
import com.marcel.pna.settings.domain.defaultCountryAlpha2Code
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val API_KEY = stringPreferencesKey("api-key")
private val COUNTRY = stringPreferencesKey("country")
private val HEADLINES_SOURCES_IDS = stringSetPreferencesKey("headlines-sources-ids")
private val HEADLINES_PER_REQUEST = intPreferencesKey("headlines-per-request")
private val LOAD_TRENDING_HEADLINES_BY = stringPreferencesKey("load-trending-headlines-by")
private val USES_DEVELOPER_API_KEYS = booleanPreferencesKey("uses-developer-api-keys")

class SettingsLocalDataSource(
    private val appConfigProvider: () -> AppConfig,
    private val settingsDataStore: DataStore<Preferences>,
    private val logger: Logger
) {
    private val defaultSettings = Settings(
        apiKey = "",
        countryAlpha2Code = defaultCountryAlpha2Code,
        headlinesSourcesIds = null,
        loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country,
        headlinesPerRequest = appConfigProvider().headlinesConfig.headlinesPerRequest,
        usesDeveloperApiKeys = true
    )

    fun getSettings(): Flow<Settings> {
        return settingsDataStore.data
            .map { preferences -> preferences.toSettings() }
            .catch {
                logger.logError(it)
                emit(defaultSettings)
            }
    }

    suspend fun updateSettings(update: SettingsUpdate): Result<SettingsError, Unit> {
        return Result.catching {
            settingsDataStore.edit { settings ->
                settings.setValues(update)
            }
        }.map { }
            .mapFailure {
                logger.logError(it)
                SettingsError.FailedToSave
            }
    }

    private fun MutablePreferences.setValues(update: SettingsUpdate) {
        update.apiKey?.let {
            this[API_KEY] = it
        }
        update.countryAlpha2Code?.let {
            this[COUNTRY] = it
        }
        update.headlinesSourcesIds?.let {
            this[HEADLINES_SOURCES_IDS] = it
        }
        update.loadTrendingHeadlinesBy?.let {
            this[LOAD_TRENDING_HEADLINES_BY] = it.name
        }
        update.headlinesPerRequest?.let {
            this[HEADLINES_PER_REQUEST] = it
        }
        update.usesDeveloperApiKeys?.let {
            this[USES_DEVELOPER_API_KEYS] = it
        }

    }

    private fun Preferences.toSettings(): Settings {
        return Settings(
            apiKey = this[API_KEY] ?: "",
            countryAlpha2Code = this[COUNTRY] ?: defaultCountryAlpha2Code,
            headlinesSourcesIds = this[HEADLINES_SOURCES_IDS],
            loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.valueOf(
                this[LOAD_TRENDING_HEADLINES_BY] ?: LoadTrendingHeadlinesBy.Country.name
            ),
            headlinesPerRequest = this[HEADLINES_PER_REQUEST]
                ?: (appConfigProvider().headlinesConfig.headlinesPerRequest),
            usesDeveloperApiKeys = this[USES_DEVELOPER_API_KEYS] == true
        )
    }
}