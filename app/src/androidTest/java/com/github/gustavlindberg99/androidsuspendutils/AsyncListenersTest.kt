package com.github.gustavlindberg99.androidsuspendutils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsyncListenersTest {
    public class TestActivity : AppCompatActivity()

    @Test
    fun testSetOnClickListenerAsync(
    ) = ActivityScenario.launch(TestActivity::class.java).use { scenario ->
        var clicked = false
        val viewId = View.generateViewId()
        scenario.onActivity { activity ->
            val view = View(activity)
            view.id = viewId
            activity.setContentView(view)
            view.setOnClickListenerAsync {
                clicked = true
            }
        }
        onView(withId(viewId)).perform(click())
        assertTrue(clicked)
    }
}