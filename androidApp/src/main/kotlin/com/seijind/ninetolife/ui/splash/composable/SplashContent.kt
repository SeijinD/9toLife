package com.seijind.ninetolife.ui.splash.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.theme.AppTheme
import com.seijind.ninetolife.ui.theme.BrandBrass
import com.seijind.ninetolife.ui.theme.BrandNavy
import kotlinx.coroutines.launch

/** Same size as the system splash icon (288dp), so the hand-off from the system splash does not jump. */
private val LogoSize = 288.dp
private val TaglineOffset = 120.dp
private val TaglinePadding = 32.dp
private const val LogoTargetScale = 1.12f
private const val LogoScaleMillis = 650
private const val TaglineFadeMillis = 400
private const val TaglineDelayMillis = 150

@Composable
internal fun SplashContent(
    tagline: String?,
    modifier: Modifier = Modifier,
) {
    val logoScale = remember { Animatable(initialValue = 1f) }
    val taglineAlpha = remember { Animatable(initialValue = 0f) }
    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = LogoTargetScale,
                animationSpec = tween(durationMillis = LogoScaleMillis, easing = FastOutSlowInEasing),
            )
        }
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = TaglineFadeMillis, delayMillis = TaglineDelayMillis),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = BrandNavy),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .size(size = LogoSize)
                .graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                },
        )
        if (tagline != null) {
            Text(
                text = tagline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = BrandBrass,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = TaglineOffset)
                    .padding(horizontal = TaglinePadding)
                    .graphicsLayer { alpha = taglineAlpha.value },
            )
        }
    }
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun SplashContentPreview() {
    AppTheme(darkTheme = false) {
        SplashContent(tagline = "Welcome to the galley")
    }
}
