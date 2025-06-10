package com.marcel.pna

import org.koin.dsl.module

val TestModule = module {
    // App config
    single<() -> AppConfig> {
        { AppConfig() }
    }

    // Logger
    single { testLogger }
}