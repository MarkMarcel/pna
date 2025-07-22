package com.marcel.pna

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_COROUTINES_SCOPE = "appCoroutinesScope"

val AppModule = module {
    single(named(APP_COROUTINES_SCOPE)) {
        CoroutineScope(SupervisorJob())
    }
    single<() -> AppConfig> {
        { AppConfig() }
    }
}