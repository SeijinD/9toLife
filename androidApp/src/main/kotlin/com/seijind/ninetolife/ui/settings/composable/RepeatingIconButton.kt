package com.seijind.ninetolife.ui.settings.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.FilledIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val HoldDelay = 400.milliseconds
private val StartInterval = 180.milliseconds
private val MinInterval = 50.milliseconds
private const val Acceleration = 0.82

/** Steps once per tap; keeps stepping, faster and faster, while held. */
@Composable
internal fun RepeatingIconButton(
    onStep: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOnStep by rememberUpdatedState(newValue = onStep)
    var didRepeat by remember { mutableStateOf(value = false) }

    LaunchedEffect(isPressed, enabled) {
        if (!isPressed || !enabled) return@LaunchedEffect
        didRepeat = false
        delay(duration = HoldDelay)
        var interval: Duration = StartInterval
        while (isActive) {
            didRepeat = true
            currentOnStep()
            delay(duration = interval)
            interval = (interval * Acceleration).coerceAtLeast(minimumValue = MinInterval)
        }
    }

    FilledIconButton(
        onClick = { if (!didRepeat) currentOnStep() },
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier,
        content = content,
    )
}
