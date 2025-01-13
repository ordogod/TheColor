package io.github.mmolosay.thecolor.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * [Dispatchers.Main] is hardcoded in some components (like `viewModelScope`).
 * Use this extension to replace it with a special [TestDispatcher].
 */
/*
 * TODO: unlike TestWatcher and Rule from JUnit4, Extensions from JUnit 5 are instantiated
 *  by the test engine. It takes away the opportunity to specify a `testDispatcher` to be used.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension : BeforeEachCallback, AfterEachCallback {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}