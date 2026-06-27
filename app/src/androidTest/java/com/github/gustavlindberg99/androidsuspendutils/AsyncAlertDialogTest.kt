package com.github.gustavlindberg99.androidsuspendutils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.gustavlindberg99.androidsuspendutils.test.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class AsyncAlertDialogTest {
    public class TestActivity : AppCompatActivity()

    /**
     * Shows an alert dialog with a positive and negative button, and returns as soon as the alert dialog is open.
     *
     * @param scenario  The activity scenario to use.
     *
     * @return A deferred value that will be completed when the alert dialog is closed.
     */
    private suspend fun showTestDialog(scenario: ActivityScenario<TestActivity>): CompletableDeferred<Boolean> {
        val resultDeferred = CompletableDeferred<Boolean>()
        scenario.onActivity { activity ->
            activity.lifecycleScope.launch(Dispatchers.Main.immediate) {
                val result = AlertDialog.Builder(activity)
                    .setTitle("Test")
                    .setMessage("Message")
                    .showAsync("Positive", "Negative")

                resultDeferred.complete(result)
            }
        }

        // Delay to avoid race conditions
        delay(200.milliseconds)

        return resultDeferred
    }

    @Test
    fun testAsyncAlertDialogPositiveButton() = runBlocking {
        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            // Launch the async alert dialog
            val resultDeferred = showTestDialog(scenario)

            // Automatically click on the button
            onView(withText("Positive")).perform(click())

            // Assert that the result is true meaning the positive button was clicked
            assertTrue(resultDeferred.await())
        }
    }

    @Test
    fun testAsyncAlertDialogNegativeButton() = runBlocking {
        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            // Launch the async alert dialog
            val resultDeferred = showTestDialog(scenario)

            // Automatically click on the button
            onView(withText("Negative")).perform(click())

            // Assert that the result is false meaning the negative button was clicked
            assertFalse(resultDeferred.await())
        }
    }

    @Test
    fun testAsyncAlertDialogResourceId() = runBlocking {
        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            // Launch the async alert dialog
            val resultDeferred = CompletableDeferred<Boolean>()
            scenario.onActivity { activity ->
                activity.lifecycleScope.launch(Dispatchers.Main.immediate) {
                    val result = AlertDialog.Builder(activity)
                        .setTitle("Test")
                        .setMessage("Message")
                        .showAsync(R.string.positive, R.string.negative)

                    resultDeferred.complete(result)
                }
            }

            // Delay to avoid race conditions
            delay(200.milliseconds)

            // Automatically click on the button
            onView(withText("Positive")).perform(click())

            // Assert that the result is true meaning the positive button was clicked
            assertTrue(resultDeferred.await())
        }
    }
}