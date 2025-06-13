package com.marcel.pna

import android.app.Application
import com.marcel.pna.core.CoreModule
import com.marcel.pna.countries.CountriesModule
import com.marcel.pna.headlines.HeadlinesModule
import com.marcel.pna.ui.UiModule
import com.marcel.pna.usersettings.SettingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class PNAMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PNAMApplication)
            modules(
                listOf(
                    AppModule,
                    CoreModule,
                    CountriesModule,
                    HeadlinesModule,
                    SettingsModule,
                    UiModule,
                )
            )
        }
    }
}