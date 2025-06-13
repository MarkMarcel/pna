package com.marcel.pna.ui.usersettings

import com.marcel.pna.AppConfig
import com.marcel.pna.MainDispatcherRule
import com.marcel.pna.core.Result
import com.marcel.pna.countries.countriesTestData
import com.marcel.pna.countries.domain.CountriesRepository
import com.marcel.pna.countries.domain.CountryError
import com.marcel.pna.countries.domain.usecases.CountriesUseCaseProvider
import com.marcel.pna.usersettings.domain.UserSettingsRepository
import com.marcel.pna.usersettings.domain.usecases.UserSettingsUseCaseProvider
import com.marcel.pna.usersettings.userSettingsDefaultTestInstance
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `When ErrorHandled intent received, Then state updates to next error`() = runTest {
        // Mock CountryRepository to return error on updateCountries
        val countriesRepository = mockk<CountriesRepository> {
            coEvery { updateCountries() } returns Result.Failure(CountryError.Network)
        }

        val userSettingsRepository = mockk<UserSettingsRepository> {
            coEvery { updateSettings(any()) } returns Result.Success(Unit)
        }

        // UseCase Providers
        val countriesUseCaseProvider = CountriesUseCaseProvider(
            backgroundDispatcher = mainDispatcherRule.testDispatcher,
            countriesRepository = countriesRepository
        )
        val userSettingsUseCaseProvider = UserSettingsUseCaseProvider(
            backgroundDispatcher = mainDispatcherRule.testDispatcher,
            userSettingsRepository = userSettingsRepository
        )
        // Create ViewModel
        val viewModel = UserSettingsScreenViewModel(
            appConfigProvider = { AppConfig() },
            countriesUseCaseProvider = countriesUseCaseProvider,
            userSettingsUseCaseProvider = userSettingsUseCaseProvider
        )
        var uiState: UserSettingsScreenUiState.Initialised? = null
        val uiStateCollectionJob = launch {
            viewModel.uiState.filterIsInstance<UserSettingsScreenUiState.Initialised>()
                .collect {
                    uiState = it
                }
        }
        // Generate first error
        viewModel.onIntent(UserSettingsIntent.UpdateCountries)
        advanceUntilIdle()
        // Check that current error is correct
        assertEquals(
            expected = UserSettingsScreenError.NetWork,
            actual = uiState?.error
        )
        // Generate second error
        viewModel.onIntent(
            UserSettingsIntent.SetTrendingHeadlinesCountry(
                country = UiCountry(
                    alpha2Code = "",
                    name = ""
                )
            )
        )
        // Handle first error
        viewModel.onIntent(UserSettingsIntent.ErrorHandled)
        advanceUntilIdle()
        // Check that current error is correct
        assertEquals(
            expected = UserSettingsScreenError.NoCountry,
            actual = uiState?.error
        )
        // Handle second error
        viewModel.onIntent(UserSettingsIntent.ErrorHandled)
        advanceUntilIdle()
        // Check that current error is correct
        assertEquals(
            expected = null,
            actual = uiState?.error
        )
        uiStateCollectionJob.cancel()
    }

    @Test
    fun `When LoadDataIntent received, Then state updates to loaded data`() = runTest {
        val appConfig = AppConfig()
        val settings = userSettingsDefaultTestInstance
        val countriesRepository = mockk<CountriesRepository> {
            every { getCountries() } returns flowOf(countriesTestData)
        }
        val userSettingsRepository = mockk<UserSettingsRepository> {
            every { getSettings() } returns flowOf(settings)
        }
        // UseCase Providers
        val countriesUseCaseProvider = CountriesUseCaseProvider(
            backgroundDispatcher = mainDispatcherRule.testDispatcher,
            countriesRepository = countriesRepository
        )
        val userSettingsUseCaseProvider = UserSettingsUseCaseProvider(
            backgroundDispatcher = mainDispatcherRule.testDispatcher,
            userSettingsRepository = userSettingsRepository
        )
        // Create ViewModel
        val locale = Locale.ENGLISH
        val viewModel = UserSettingsScreenViewModel(
            appConfigProvider = { appConfig },
            countriesUseCaseProvider = countriesUseCaseProvider,
            userSettingsUseCaseProvider = userSettingsUseCaseProvider
        )
        viewModel.onIntent(UserSettingsIntent.LoadData(languageCode = locale.language))
        // Expected Results
        val expectedUiState = UserSettingsScreenModelState.Initialised(
            countries = countriesTestData,
            generateNewsApiUrl = appConfig.servicesConfig.newsApiKeyGenerationUrl,
            languageCode = locale.language
        )
            .toScreenModelState(loadedSettings = settings)
            .toUiState()
        val uiState = viewModel.uiState.filterIsInstance<UserSettingsScreenUiState.Initialised>()
            .first()
        assertEquals(
            expected = expectedUiState,
            actual = uiState
        )
    }
}