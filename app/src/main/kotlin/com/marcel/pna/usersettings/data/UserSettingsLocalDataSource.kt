package com.marcel.pna.usersettings.data

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
import com.marcel.pna.usersettings.domain.LoadTrendingHeadlinesBy
import com.marcel.pna.usersettings.domain.UserSettings
import com.marcel.pna.usersettings.domain.UserSettingsError
import com.marcel.pna.usersettings.domain.UserSettingsUpdate
import com.marcel.pna.usersettings.domain.defaultCountryAlpha2Code
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val API_KEY = stringPreferencesKey("api-key")
private val HEADLINES_PER_REQUEST = intPreferencesKey("headlines-per-request")
private val LOAD_TRENDING_HEADLINES_BY = stringPreferencesKey("load-trending-headlines-by")
private val TRENDING_HEADLINES_SOURCES_IDS = stringSetPreferencesKey("headlines-sources-ids")
private val TRENDING_HEADLINES_COUNTRY = stringPreferencesKey("country")
private val USES_DEVELOPER_API_KEYS = booleanPreferencesKey("uses-developer-api-keys")

class UserSettingsLocalDataSource(
    private val appConfigProvider: () -> AppConfig,
    private val settingsDataStore: DataStore<Preferences>,
    private val logger: Logger
) {
    private val defaultUserSettings = UserSettings(
        apiKey = "",
        loadTrendingHeadlinesBy = LoadTrendingHeadlinesBy.Country(alpha2Code = defaultCountryAlpha2Code),
        headlinesPerRequest = appConfigProvider().headlinesConfig.headlinesPerRequest,
        usesDeveloperApiKeys = true
    )

    fun getSettings(): Flow<UserSettings> {
        return settingsDataStore.data
            .map { preferences -> preferences.toSettings() }
            .catch {
                logger.logError(it)
                emit(defaultUserSettings)
            }
    }

    suspend fun updateSettings(update: UserSettingsUpdate): Result<UserSettingsError, Unit> {
        return Result.catching {
            settingsDataStore.edit { settings ->
                settings.setValues(update)
            }
        }.map { }
            .mapFailure {
                logger.logError(it)
                UserSettingsError.FailedToSave
            }
    }

    private fun MutablePreferences.setValues(update: UserSettingsUpdate) {
        update.apiKey?.let {
            this[API_KEY] = it
        }
        update.loadTrendingHeadlinesBy?.let {
            when (it) {
                is LoadTrendingHeadlinesBy.Country -> {
                    this[LOAD_TRENDING_HEADLINES_BY] = TRENDING_HEADLINES_COUNTRY.name
                    this[TRENDING_HEADLINES_COUNTRY] = it.alpha2Code
                }

                is LoadTrendingHeadlinesBy.Sources -> {
                    this[LOAD_TRENDING_HEADLINES_BY] = TRENDING_HEADLINES_SOURCES_IDS.name
                    this[TRENDING_HEADLINES_SOURCES_IDS] = it.sourceIds
                }
            }
        }
        update.loadTrendingHeadlinesBy?.let {
        }
        update.headlinesPerRequest?.let {
            this[HEADLINES_PER_REQUEST] = it
        }
        update.usesDeveloperApiKeys?.let {
            this[USES_DEVELOPER_API_KEYS] = it
        }

    }

    private fun Preferences.toSettings(): UserSettings {
        val loadTrendingHeadlinesBy: LoadTrendingHeadlinesBy =
            this[LOAD_TRENDING_HEADLINES_BY]?.let {
                when {
                    (it == TRENDING_HEADLINES_COUNTRY.name && this[TRENDING_HEADLINES_COUNTRY] != null) -> {
                        this[TRENDING_HEADLINES_COUNTRY]?.let { alpha2Code ->
                            LoadTrendingHeadlinesBy.Country(alpha2Code = alpha2Code)
                        }
                    }

                    else -> {
                        this[TRENDING_HEADLINES_SOURCES_IDS]?.let { sourceIds ->
                            LoadTrendingHeadlinesBy.Sources(sourceIds = sourceIds)
                        }
                    }
                }
            } ?: defaultUserSettings.loadTrendingHeadlinesBy
        return UserSettings(
            apiKey = this[API_KEY] ?: "",
            loadTrendingHeadlinesBy = loadTrendingHeadlinesBy,
            headlinesPerRequest = this[HEADLINES_PER_REQUEST]
                ?: (appConfigProvider().headlinesConfig.headlinesPerRequest),
            usesDeveloperApiKeys = this[USES_DEVELOPER_API_KEYS] == true
        )
    }
}