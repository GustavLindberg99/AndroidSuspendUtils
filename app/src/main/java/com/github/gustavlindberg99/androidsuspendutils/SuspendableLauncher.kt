package com.github.gustavlindberg99.androidsuspendutils

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Wrapper class for ActivityResultLauncher that allows to wait for an activity result in a suspend function. Must be constructed in an activity's constructor.
 *
 * @param context   The activity context.
 * @param contract  The contract to use.
 */
class SuspendableLauncher<I, O>(
    context: ComponentActivity,
    contract: ActivityResultContract<I, O>
) {
    private var _continuation: Continuation<O>? = null
    private val _mutex = Mutex()
    private val _launcher = context.registerForActivityResult(contract, {
        val continuation = this._continuation
        this._continuation = null
        continuation?.resume(it)
    })

    /**
     * Launches the activity and waits for the result. If another activity has already been launched with this [SuspendableLauncher] object and hasn't finished yet, queues the new request and launches it when the old one finishes.
     *
     * @param input The input to pass to the activity.
     *
     * @return The result of the activity.
     */
    public suspend fun launch(input: I): O = this._mutex.withLock {
        suspendCancellableCoroutine {
            this._continuation = it
            it.invokeOnCancellation {
                this._continuation = null
            }
            this._launcher.launch(input)
        }
    }
}