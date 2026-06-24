package com.github.gustavlindberg99.androidsuspendutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext as originalWithContext
import kotlinx.coroutines.async as originalAsync
import kotlinx.coroutines.launch as originalLaunch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Similar to `kotlinx.coroutines.launch`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.
 */
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend (CoroutineScope) -> Unit
): Job {
    return this.originalLaunch(context, start, { block(this) })
}

/**
 * Similar to `kotlinx.coroutines.async`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.
 */
public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend (CoroutineScope) -> T
): Deferred<T> {
    return this.originalAsync(context, start, { block(this) })
}

/**
 * Similar to `kotlinx.coroutines.withContext`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.
 */
public suspend fun <T> withContext(
    context: CoroutineContext,
    block: suspend (CoroutineScope) -> T
): T {
    return originalWithContext(context, { block(this) })
}