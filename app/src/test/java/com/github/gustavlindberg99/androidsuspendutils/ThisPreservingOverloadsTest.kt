package com.github.gustavlindberg99.androidsuspendutils

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ThisPreservingOverloadsTest {
    @Test
    fun testFlow() = runTest {
        val flow = flow {
            it.emit(1)
            it.emit(2)
            it.emit(3)
        }
        val result = flow.toList()
        assertEquals(result, listOf(1, 2, 3))
    }
}