package com.github.gustavlindberg99.androidsuspendutils

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.Closeable

class UseWithContextTest {
    private class MockCloseable : Closeable {
        public var closed = false
        public var value = "unmodified"

        public override fun close() {
            this.closed = true
        }
    }

    @Test
    fun testUseWithContext() = runTest {
        val mockCloseable = MockCloseable()
        val testDispatcher = StandardTestDispatcher(testScheduler)

        val result = mockCloseable.useWithContext(testDispatcher) {
            it.value = "modified"
            "success"
        }

        assertEquals(result, "success")
        assertEquals(mockCloseable.value, "modified")
        assertTrue(mockCloseable.closed)
    }
}