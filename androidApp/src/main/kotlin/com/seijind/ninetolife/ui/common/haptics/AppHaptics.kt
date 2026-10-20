package com.seijind.ninetolife.ui.common.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

private val CelebrationTimings = longArrayOf(0, 60, 80, 60, 80, 160)
private val CelebrationAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
private const val NoRepeat = -1

/** One place that knows whether haptics are on, so call sites never check the setting. */
internal class AppHaptics(
    private val enabled: Boolean,
    private val hapticFeedback: HapticFeedback,
    private val vibrator: Vibrator?,
) {
    fun tick(): Unit = perform(type = HapticFeedbackType.SegmentTick)

    fun confirm(): Unit = perform(type = HapticFeedbackType.Confirm)

    fun reject(): Unit = perform(type = HapticFeedbackType.Reject)

    fun toggle(on: Boolean): Unit = perform(type = if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)

    fun celebrate() {
        if (!enabled) return
        vibrator?.vibrate(
            VibrationEffect.createWaveform(CelebrationTimings, CelebrationAmplitudes, NoRepeat),
        )
    }

    private fun perform(type: HapticFeedbackType) {
        if (enabled) hapticFeedback.performHapticFeedback(hapticFeedbackType = type)
    }
}

internal val LocalAppHaptics = staticCompositionLocalOf<AppHaptics> {
    error("LocalAppHaptics is not provided")
}

@Composable
internal fun rememberAppHaptics(enabled: Boolean): AppHaptics {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    return remember(enabled, context, hapticFeedback) {
        AppHaptics(enabled = enabled, hapticFeedback = hapticFeedback, vibrator = context.vibrator())
    }
}

private fun Context.vibrator(): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        getSystemService(Vibrator::class.java)
    }
