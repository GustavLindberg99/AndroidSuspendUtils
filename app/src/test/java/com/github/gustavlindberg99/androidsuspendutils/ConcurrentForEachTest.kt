package com.github.gustavlindberg99.androidsuspendutils

import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConcurrentForEachTest {
    private class TestException : Exception()

    private val _testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(_testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testConcurrentForEach() = runTest {
        val items = listOf(1, 2, 3)
        val results = mutableListOf<Int>()
        val context = TestLifecycleOwner()

        items.concurrentForEach(context) {
            results.add(it)
        }

        assertEquals(results.toSet(), items.toSet())
    }

    @Test
    fun testConcurrentForEachException() = runTest {
        val items = listOf(1, 2, 3)
        val results = mutableListOf<Int>()
        val context = TestLifecycleOwner()

        val exception = try {
            items.concurrentForEach(context) {
                if (it == 2) {
                    throw TestException()
                }
                results.add(it)
            }
            null
        }
        catch (e: TestException) {
            e
        }

        assertNotNull(exception)
        assertEquals(results.toSet(), setOf(1, 3))
    }
}