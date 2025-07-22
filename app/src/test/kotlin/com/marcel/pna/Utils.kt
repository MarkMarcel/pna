package com.marcel.pna

import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.Logger
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.mock.declare

/**
 * JUnit rule that sets the main coroutine dispatcher to a [TestDispatcher] for testing.
 *
 * Uses [StandardTestDispatcher] by default, but a custom dispatcher can be provided.
 * Resets the main dispatcher after the test finishes.
 *
 * Reuse [testDispatcher] in your test body or create new dispatchers if needed.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


val testLogger = mockk<Logger>(relaxed = true) {
    every {
        logError(any())
    } returns Unit
}

fun KoinTest.declareTestDispatchers(
    testScope: TestScope
) {
    val testDispatcher =
        StandardTestDispatcher(testScope.testScheduler) // use same scheduler as testScope
    declare<CoroutineDispatcher>(named(BACKGROUND_DISPATCHER)) { testDispatcher }
    declare<CoroutineDispatcher>(named(IO_DISPATCHER)) { testDispatcher }
}