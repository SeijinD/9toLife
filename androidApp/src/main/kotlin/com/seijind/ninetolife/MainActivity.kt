package com.seijind.ninetolife

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.seijind.ninetolife.quickqr.ActionOpenQr
import com.seijind.ninetolife.ui.app.composable.App

/** AppCompatActivity for per-app language support on API < 33. */
class MainActivity : AppCompatActivity() {

    /** Time of the last QR shortcut tap; 0 means none. A time, not a counter, so it stays unique across recreation. */
    private var quickQrRequest by mutableLongStateOf(value = 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // A restored activity still carries the old intent; only a fresh launch counts as a tap.
        if (savedInstanceState == null) handleQuickQr(intent = intent)
        setContent {
            App(quickQrRequest = { quickQrRequest })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleQuickQr(intent = intent)
    }

    private fun handleQuickQr(intent: Intent) {
        if (intent.action == ActionOpenQr) quickQrRequest = SystemClock.elapsedRealtime()
    }
}
