package com.seijind.ninetolife.ui.splash.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalResources
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.app.navigation.SplashKey
import com.seijind.ninetolife.ui.splash.composable.SplashContent
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private val SplashDuration = 2.seconds

internal fun EntryProviderScope<NavKey>.splashEntry(
    isReady: () -> Boolean,
    seriousMode: () -> Boolean,
    onFinished: () -> Unit,
) {
    entry<SplashKey> {
        val resources = LocalResources.current
        val funTagline = remember { resources.getStringArray(R.array.splash_taglines_fun).random() }
        val currentOnFinished = rememberUpdatedState(newValue = onFinished)
        val ready = isReady()

        // Waits for the settings so serious mode never flashes a joke.
        LaunchedEffect(ready) {
            if (!ready) return@LaunchedEffect
            delay(duration = SplashDuration)
            currentOnFinished.value()
        }

        SplashContent(
            tagline = when {
                !ready -> null
                seriousMode() -> resources.getString(R.string.splash_tagline_serious)
                else -> funTagline
            },
        )
    }
}
