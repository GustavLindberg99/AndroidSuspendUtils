package com.github.gustavlindberg99.androidsuspendutils

import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Shows the given alert dialog with a positive and negative button, and suspends the coroutine until the alert dialog is closed.
 *
 * @param positiveButtonText    The text to display on the positive button.
 * @param negativeButtonText    The text to display on the negative button.
 *
 * @return True if the user clicked "Yes", false otherwise.
 */
public suspend fun AlertDialog.Builder.showAsync(
    positiveButtonText: String,
    negativeButtonText: String
): Boolean = suspendCancellableCoroutine { continuation ->
    this.setPositiveButton(positiveButtonText, { _, _ -> continuation.resume(true) })
        .setNegativeButton(negativeButtonText, { _, _ -> continuation.resume(false) })
        .setOnCancelListener { continuation.resume(false) }
        .show()
}

/**
 * Shows the given alert dialog with a positive and negative button, and suspends the coroutine until the alert dialog is closed.
 *
 * @param positiveButtonTextId  The resource ID of the text to display on the positive button.
 * @param negativeButtonTextId  The resource ID of the text to display on the negative button.
 *
 * @return True if the user clicked "Yes", false otherwise.
 */
public suspend fun AlertDialog.Builder.showAsync(
    positiveButtonTextId: Int,
    negativeButtonTextId: Int
): Boolean = suspendCancellableCoroutine { continuation ->
    this.setPositiveButton(positiveButtonTextId, { _, _ -> continuation.resume(true) })
        .setNegativeButton(negativeButtonTextId, { _, _ -> continuation.resume(false) })
        .setOnCancelListener { continuation.resume(false) }
        .show()
}