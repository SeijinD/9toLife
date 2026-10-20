package com.seijind.ninetolife.ui.home.composable

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.theme.QrBackground
import com.seijind.ninetolife.ui.theme.QrForeground

private val QrMaxWidth = 520.dp
private val QrPadding = 24.dp
private val Spacing = 24.dp
private const val HintAlpha = 0.6f
private const val FullBrightness = 1f

/** Full-screen QR for the reader at the door: white field, max brightness, no system bars. */
@Composable
internal fun ExpandedQr(
    payload: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScannableScreenEffect()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = QrBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            )
            .padding(all = QrPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = Spacing, alignment = Alignment.CenterVertically),
    ) {
        QrImage(
            payload = payload,
            contentDescription = stringResource(id = R.string.home_qr_cd),
            modifier = Modifier
                .widthIn(max = QrMaxWidth)
                .fillMaxWidth()
                .aspectRatio(ratio = 1f),
        )
        Text(
            text = stringResource(id = R.string.home_qr_close_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = QrForeground.copy(alpha = HintAlpha),
        )
    }
}

@Composable
private fun ScannableScreenEffect() {
    val window = LocalActivity.current?.window ?: return
    DisposableEffect(window) {
        val previousBrightness = window.attributes.screenBrightness
        window.attributes = window.attributes.apply { screenBrightness = FullBrightness }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            window.attributes = window.attributes.apply { screenBrightness = previousBrightness }
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
