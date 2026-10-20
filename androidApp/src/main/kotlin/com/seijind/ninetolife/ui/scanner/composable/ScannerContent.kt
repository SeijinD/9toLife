package com.seijind.ninetolife.ui.scanner.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.scanner.model.CameraPermission
import com.seijind.ninetolife.ui.scanner.model.ScannerActions
import com.seijind.ninetolife.ui.theme.AppTheme
import com.seijind.ninetolife.ui.theme.BrandBrass

private val ViewfinderSize = 260.dp
private val ViewfinderStroke = 4.dp
private val ViewfinderCorner = 28.dp
private val ContentPadding = 24.dp
private val HintBottomPadding = 48.dp
private val Spacing = 16.dp
private const val ScrimAlpha = 0.45f

/** [cameraPreview] is a slot so the screen stays previewable without a camera. */
@Composable
internal fun ScannerContent(
    permission: CameraPermission,
    actions: ScannerActions,
    modifier: Modifier = Modifier,
    cameraPreview: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black),
    ) {
        when (permission) {
            CameraPermission.GRANTED -> {
                cameraPreview()
                Viewfinder(modifier = Modifier.align(alignment = Alignment.Center))
                Text(
                    text = stringResource(id = R.string.scanner_hint),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .safeDrawingPadding()
                        .padding(bottom = HintBottomPadding, start = ContentPadding, end = ContentPadding),
                )
            }
            CameraPermission.REQUESTABLE, CameraPermission.BLOCKED -> PermissionPrompt(
                permission = permission,
                onGrantClick = actions.onGrantPermissionClick,
                onOpenSettingsClick = actions.onOpenSettingsClick,
                modifier = Modifier.align(alignment = Alignment.Center),
            )
        }
        IconButton(
            onClick = actions.onClose,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = ScrimAlpha),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .safeDrawingPadding()
                .padding(all = Spacing),
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = stringResource(id = R.string.common_close))
        }
    }
}

@Composable
private fun Viewfinder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size = ViewfinderSize)) {
        val stroke = ViewfinderStroke.toPx()
        drawRoundRect(
            color = BrandBrass,
            topLeft = Offset(x = stroke / 2, y = stroke / 2),
            size = Size(width = size.width - stroke, height = size.height - stroke),
            cornerRadius = CornerRadius(x = ViewfinderCorner.toPx(), y = ViewfinderCorner.toPx()),
            style = Stroke(width = stroke),
        )
    }
}

@Composable
private fun PermissionPrompt(
    permission: CameraPermission,
    onGrantClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val blocked = permission == CameraPermission.BLOCKED
    Column(
        modifier = modifier.padding(all = ContentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = Spacing),
    ) {
        Text(
            text = stringResource(id = R.string.scanner_permission_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.scanner_permission_body),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Button(onClick = if (blocked) onOpenSettingsClick else onGrantClick) {
            Text(
                text = stringResource(id = if (blocked) R.string.scanner_permission_settings else R.string.scanner_permission_grant),
            )
        }
    }
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun ScannerContentPreview() {
    AppTheme(darkTheme = true) {
        ScannerContent(
            permission = CameraPermission.REQUESTABLE,
            actions = ScannerActions(onClose = {}, onGrantPermissionClick = {}, onOpenSettingsClick = {}),
            cameraPreview = {},
        )
    }
}
