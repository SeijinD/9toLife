package com.seijind.ninetolife.ui.home.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.theme.AppTheme
import com.seijind.ninetolife.ui.theme.BrandBrass
import com.seijind.ninetolife.ui.theme.ConfettiBlue
import com.seijind.ninetolife.ui.theme.ConfettiGreen
import com.seijind.ninetolife.ui.theme.ConfettiRed
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ParticleCount = 90
private const val ConfettiMillis = 2800
private const val ScrimAlpha = 0.55f
private const val TitleStartScale = 0.4f
private const val MaxSpinDegrees = 720f
private const val MinSpeed = 0.7f
private const val SpeedRange = 0.6f
private const val MaxStartDelay = 0.3f
private val ConfettiColors = listOf(BrandBrass, ConfettiRed, ConfettiBlue, ConfettiGreen, Color.White)
private val ParticleWidth = 7.dp
private val ParticleHeight = 4.dp
private val TextPadding = 32.dp
private val TextSpacing = 8.dp

private data class Particle(
    val startX: Float,
    val drift: Float,
    val speed: Float,
    val spin: Float,
    val delay: Float,
    val color: Color,
)

/** End-of-shift overlay. Dismisses itself when the confetti has fallen, or on tap. */
@Composable
internal fun FreedomCelebration(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnFinished = rememberUpdatedState(newValue = onFinished)
    val progress = remember { Animatable(initialValue = 0f) }
    val titleScale = remember { Animatable(initialValue = TitleStartScale) }
    val particles = remember {
        List(size = ParticleCount) {
            Particle(
                startX = Random.nextFloat(),
                drift = Random.nextFloat() - 0.5f,
                speed = MinSpeed + Random.nextFloat() * SpeedRange,
                spin = (Random.nextFloat() - 0.5f) * MaxSpinDegrees,
                delay = Random.nextFloat() * MaxStartDelay,
                color = ConfettiColors.random(),
            )
        }
    }
    LaunchedEffect(Unit) {
        launch {
            titleScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            )
        }
        progress.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = ConfettiMillis, easing = LinearEasing))
        currentOnFinished.value()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = ScrimAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { currentOnFinished.value() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particleSize = Size(width = ParticleWidth.toPx(), height = ParticleHeight.toPx())
            particles.forEach { particle ->
                val t = ((progress.value - particle.delay) / (1f - particle.delay)).coerceIn(minimumValue = 0f, maximumValue = 1f)
                if (t <= 0f) return@forEach
                val x = (particle.startX + particle.drift * t) * size.width
                val y = -particleSize.height + t * particle.speed * (size.height + particleSize.height * 2)
                rotate(degrees = particle.spin * t, pivot = Offset(x = x, y = y)) {
                    drawRect(color = particle.color, topLeft = Offset(x = x, y = y), size = particleSize)
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = TextSpacing),
            modifier = Modifier
                .padding(horizontal = TextPadding)
                .graphicsLayer {
                    scaleX = titleScale.value
                    scaleY = titleScale.value
                },
        ) {
            Text(
                text = stringResource(id = R.string.home_freedom_title),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = BrandBrass,
                textAlign = TextAlign.Center,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.displayLarge.fontSize),
            )
            Text(
                text = stringResource(id = R.string.home_freedom_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun FreedomCelebrationPreview() {
    AppTheme(darkTheme = true) {
        FreedomCelebration(onFinished = {})
    }
}
