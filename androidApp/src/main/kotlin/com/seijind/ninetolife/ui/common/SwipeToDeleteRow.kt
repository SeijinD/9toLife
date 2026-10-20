package com.seijind.ninetolife.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val IconPadding = 16.dp

/** Swiping from the end deletes, on a red background like iOS swipe actions. */
@Composable
internal fun SwipeToDeleteRow(
    onDelete: () -> Unit,
    deleteLabel: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit,
) {
    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        enableDismissFromStartToEnd = false,
        onDismiss = { onDelete() },
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = shape)
                    .background(color = MaterialTheme.colorScheme.error)
                    .padding(horizontal = IconPadding),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = deleteLabel,
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
        },
    ) {
        content()
    }
}
