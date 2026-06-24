package com.github.gustavlindberg99.androidsuspendutils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

/**
 * Runs the given action on each element of the collection concurrently. Returns when all actions have finished.
 *
 * @param context   The context to run the action in.
 * @param limit     The maximum number of actions to run concurrently. Can be useful for large collections to avoid out of memory errors.
 * @param action    The action to run on each element.
 *
 * @throws Exception If an exception is thrown while running the action. The action will still be run on the remaining elements.
 */
public suspend fun <T> Collection<T>.concurrentForEach(
    context: LifecycleOwner,
    limit: Int = Int.MAX_VALUE,
    action: suspend (T) -> Unit
) {
    val promises = mutableSetOf<Deferred<Exception?>>()
    var error: Exception? = null
    for (element in this) {
        if (promises.size >= limit) {
            error = error ?: promises.awaitAll().firstOrNull { it != null }
            promises.clear()
        }
        val promise = context.lifecycleScope.async {
            try {
                action(element)
                return@async null
            }
            catch (e: Exception) {
                return@async e
            }
        }
        promises.add(promise)
    }
    error = error ?: promises.awaitAll().firstOrNull { it != null }
    if (error != null) {
        throw error
    }
}