package com.marcel.pna

import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.Logger
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.mock.declare

val testLogger = mockk<Logger>(relaxed = true) {
    every {
        logError(any())
    } returns Unit
}

internal fun KoinTest.declareTestDispatchers(
    testScope: TestScope
) {
    val testDispatcher =
        StandardTestDispatcher(testScope.testScheduler) // use same scheduler as testScope
    declare<CoroutineDispatcher>(named(BACKGROUND_DISPATCHER)) { testDispatcher }
    declare<CoroutineDispatcher>(named(IO_DISPATCHER)) { testDispatcher }
}