package com.seijind.ninetolife.ui.home.composable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
internal fun QrImage(
    payload: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = rememberQrCodePainter(data = payload),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}
