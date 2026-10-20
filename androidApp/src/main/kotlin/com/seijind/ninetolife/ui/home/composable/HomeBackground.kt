package com.seijind.ninetolife.ui.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

/** Keeps text on top of any photo readable in both themes. */
private const val PhotoScrimAlpha = 0.62f
private const val GradientTintAlpha = 0.35f

@Composable
internal fun HomeBackground(
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier.fillMaxSize()) {
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colors.background.copy(alpha = PhotoScrimAlpha)),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.primaryContainer.copy(alpha = GradientTintAlpha),
                                colors.background,
                                colors.background,
                            ),
                        ),
                    ),
            )
        }
    }
}
