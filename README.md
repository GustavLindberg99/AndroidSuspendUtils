# AndroidSuspendUtils

This library contains several utility functions and classes to use Kotlin `suspend` functions in Android.

## Installation

To use this library in your Android project, add the following to `settings.gradle` (if not already done):

```gradle
dependencyResolutionManagement {
    //...
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

And add the following to `app/build.gradle`:

```gradle
dependencies {
    //...
    implementation 'com.github.gustavlindberg99:androidsuspendutils:1.1.0'
}
```

This library requires at least API version 16.

## Documentation
### Extension functions

This library provides extension functions to various Android/Kotlin classes making it easier to work with Kotlin suspend functions in Android:

- `suspend fun AlertDialog.Builder.showAsync(positiveButtonText: String, negativeButtonText: String): Boolean`

    Shows the given alert dialog with a positive and negative button, and suspends the coroutine until the alert dialog is closed. It returns `true` if the positive button was clicked, and false otherwise.

    Example:

    ```kotlin
    import com.github.gustavlindberg99.androidsuspendutils.showAsync

    val result = AlertDialog.Builder(this)
        .setTitle("Your title")
        .setMessage("Your message")
        .showAsync("Yes", "No")    // You can use strings or resource IDs here

    if (result) {
        println("Yes was clicked")
    }
    else {
        println("No was clicked")
    }
    ```

- `suspend fun AlertDialog.Builder.showAsync(positiveButtonTextId: Int, negativeButtonTextId: Int): Boolean`

    Similar to the above but accepts resource IDs instead of strings.

- `fun View.setOnClickListenerAsync(l: suspend (View) -> Unit)`

    Similar to `View.setOnClickListener`, but allows the lambda parameter to be a suspend function, and starts a new coroutine for it when the view is clicked.

    `view.setOnClickListenerAsync{...}` is syntactic sugar for `view.setOnClickListener{lifecycleScope.launch{...}}`.

- `suspend fun <T> Collection<T>.concurrentForEach(context: LifecycleOwner, limit: Int = Int.MAX_VALUE, action: suspend (T) -> Unit)`

    Runs the given action on each element of the collection concurrently. Returns when all actions have finished.

    The `limit` parameter allows to specify the maximum number of actions to run concurrently. Can be useful for large collections to avoid out of memory errors.

    If the lambda throws an exception for any of the elements, `concurrentForEach` will finish running for the remaining elements, then propagate that exception to its caller.

    Example:

    ```kotlin
    import com.github.gustavlindberg99.androidsuspendutils.concurrentForEach

    val list = listOf(1, 2, 3)
    list.concurrentForEach(context) {
        withContext(Dispatchers.IO) {
            // You can do an HTTP request here, in that case
            // each request will be run simultaneously.
        }
    }
    ```

- `fun CoroutineScope.launch(context: CoroutineContext = EmptyCoroutineContext, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend (CoroutineScope) -> Unit): Job`

    Similar to `kotlinx.coroutines.launch`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.

    This can be very useful in activities since `this` often needs to be passed around as context. This function allows you to write simply `this` rather than `this@SomeActivity` in those cases. For example:

    ```kotlin
    import com.github.gustavlindberg99.androidsuspendutils.launch    // Instead of kotlinx.coroutines.launch

    lifecycleScope.launch {
        // No need to write this@SomeActivity
        Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show()
    }
    ```

- `fun <T> CoroutineScope.async(context: CoroutineContext = EmptyCoroutineContext, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend (CoroutineScope) -> T): Deferred<T>`

    Similar to `kotlinx.coroutines.async`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.

    For an example, see `launch` above.

- `suspend fun <T> withContext(context: CoroutineContext, block: suspend (CoroutineScope) -> T): T`

    Similar to `kotlinx.coroutines.withContext`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `CoroutineScope` in the lambda, access it with `it` rather than `this`.

    For an example, see `launch` above.

- `fun <T> flow(block: suspend (FlowCollector<T>) -> Unit): Flow<T>`

    Similar to `kotlinx.coroutines.flow`, but preserves `this` from the outer scope rather than rebinding to the `CoroutineScope` object. If you need the `FlowCollector` in the lambda, access it with `it` rather than `this`.

    Example:

    ```kotlin
    import com.github.gustavlindberg99.androidsuspendutils.flow    // Instead of kotlinx.coroutines.flow

    flow {
        // No need to write this@SomeActivity
        Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show()
  
        // Use `it.emit` to emit values
        it.emit(1)
        it.emit(2)
    }
    ```
  
    If you don't like this and just want to write `emit(...)` instead (even if it means writing `this@SomeActivity` when you need the activity), you can use the original `kotlinx.coroutines.flow` simply by importing `kotlinx.coroutines.flow` instead of `com.github.gustavlindberg99.androidsuspendutils.flow`. Of course this applies to the other `this`-preserving overloads as well.

- `suspend fun <T : Closeable?, R> T.useWithContext(context: CoroutineContext, block: suspend (T) -> R): R`

    Executes the given block of code with the given context, and closes the input stream after the block is executed. Useful for input streams that read from the network.

    `stream.useWithContext(context){...}` is syntactic sugar for `stream.use{withContext(context){...}}`.

### `SuspendableLauncher` class

`SuspendableLauncher` is a wrapper class for `ActivityResultLauncher` that allows to wait for an activity result in a suspend function.

- `SuspendableLauncher<I, O>(context: ComponentActivity, contract: ActivityResultContract<I, O>)`

    Constructor. Must be constructed in an activity's constructor, as it calls `registerForActivityResult` internally.

- `suspend fun launch(input: I): O`

    Launches the activity and waits for the result. If another activity has already been launched with this `SuspendableLauncher` object and hasn't finished yet, queues the new request and launches it when the old one finishes.

Example:

```kotlin
import com.github.gustavlindberg99.androidsuspendutils.SuspendableLauncher

class MainActivity : AppCompatActivity() {
    private val _launcher = SuspendableLauncher(
        this,
        ActivityResultContracts.StartActivityForResult()
    )

    private suspend fun launchSecondaryActivity() {
        // SecondaryActivity is the activity that should be launched
        val intent = Intent(this, SecondaryActivity::class.java)
        intent.putExtra("input", "Data to send to SecondaryActivity")

        // Launches SecondaryActivity and waits for it to finish.
        val result = _launcher.launch(intent)

        // Assuming SecondaryActivity has called `setResult(RESULT_OK, resultIntent)`,
        // where `resultIntent` is an intent containing an `"output"` extra.
        val output = result.data?.getStringExtra("output")
        println("Data send from SecondaryActivity: $output")
    }
}
```
