package com.seijind.ninetolife.ui.home.composable

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R

private val ButtonSize = 120.dp
private val IconSize = 56.dp
private val TextPadding = 32.dp
private val ContentSpacing = 12.dp
private val ButtonBottomSpacing = 20.dp
private const val PulseMillis = 1800
private const val PulseMaxScale = 1.45f
private const val PulseStartAlpha = 0.35f

@Composable
internal fun AddQrPrompt(
    seriousMode: Boolean,
    onAddQrClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "addQrPulse")
    val pulseSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis = PulseMillis, easing = LinearOutSlowInEasing),
        repeatMode = RepeatMode.Restart,
    )
    val pulseScale = pulse.animateFloat(initialValue = 1f, targetValue = PulseMaxScale, animationSpec = pulseSpec, label = "scale")
    val pulseAlpha = pulse.animateFloat(initialValue = PulseStartAlpha, targetValue = 0f, animationSpec = pulseSpec, label = "alpha")

    Column(
        modifier = modifier.padding(horizontal = TextPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = ContentSpacing),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = ButtonBottomSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(size = ButtonSize)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                        alpha = pulseAlpha.value
                    }
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            )
            FilledIconButton(
                onClick = onAddQrClick,
                modifier = Modifier.size(size = ButtonSize),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.home_add_qr_cd),
                    modifier = Modifier.size(size = IconSize),
                )
            }
        }
        Text(
            text = stringResource(id = R.string.home_add_qr_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(
                id = if (seriousMode) R.string.home_add_qr_subtitle_serious else R.string.home_add_qr_subtitle_fun,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
