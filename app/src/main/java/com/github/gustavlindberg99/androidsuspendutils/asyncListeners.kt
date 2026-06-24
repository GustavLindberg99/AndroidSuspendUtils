package com.github.gustavlindberg99.androidsuspendutils

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope

/**
 * Finds the ComponentActivity from a context, unwrapping it if necessary.
 */
private fun Context.findComponentActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Similar to `View.setOnClickListener`, but allows the lambda parameter to be a suspend function, and starts a new coroutine for it when the view is clicked.
 *
 * @param l The callback that will run.
 */
public fun View.setOnClickListenerAsync(l: suspend (View) -> Unit) {
    val context = this.context.findComponentActivity()
        ?: throw IllegalArgumentException("setOnClickListenerAsync may only be called on a View inside a ComponentActivity")
    this.setOnClickListener { view ->
        context.lifecycleScope.launch { l(view) }
    }
}