package com.github.gustavlindberg99.androidsuspendutils

import java.io.Closeable
import kotlin.coroutines.CoroutineContext

/**
 * Executes the given block of code with the given context, and closes the input stream after the block is executed. Useful for input streams that read from the network.
 *
 * @param context   The context to use.
 * @param block     The block of code to execute.
 *
 * @return The result of the block.
 */
public suspend fun <T : Closeable?, R> T.useWithContext(
    context: CoroutineContext,
    block: suspend (T) -> R
): R {
    return this.use {
        withContext(context) { block(this) }
    }
}