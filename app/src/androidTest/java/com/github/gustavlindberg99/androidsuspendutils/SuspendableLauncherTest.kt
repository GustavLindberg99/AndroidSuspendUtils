package com.github.gustavlindberg99.androidsuspendutils

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuspendableLauncherTest {
    public class TestActivity : AppCompatActivity() {
        public val launcher = SuspendableLauncher(
            this,
            ActivityResultContracts.StartActivityForResult()
        )
    }

    public class LaunchableTestActivity : AppCompatActivity() {
        companion object {
            public const val INPUT = "input"
            public const val OUTPUT = "output"
        }

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val inputData = this.intent.getStringExtra(INPUT)
            val outputData = inputData?.uppercase()
            this.setResult(RESULT_OK, this.intent.putExtra(OUTPUT, outputData))
            this.finish()
        }
    }

    @Test
    fun testSuspendableLauncher(): Unit = runBlocking {
        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            lateinit var activity: TestActivity
            scenario.onActivity { activity = it }
            val intent = Intent(activity, LaunchableTestActivity::class.java)
            intent.putExtra(LaunchableTestActivity.INPUT, "test")
            val result = activity.launcher.launch(intent)
            assertEquals(AppCompatActivity.RESULT_OK, result.resultCode)
            assertEquals("TEST", result.data?.getStringExtra(LaunchableTestActivity.OUTPUT))
        }
    }
}