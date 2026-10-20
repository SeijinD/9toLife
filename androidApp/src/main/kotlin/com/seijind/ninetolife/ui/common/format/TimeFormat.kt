package com.seijind.ninetolife.ui.common.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.seijind.ninetolife.format.formatDuration
import com.seijind.ninetolife.format.formatTimeOfDay

@Composable
internal fun formatTimeOfDay(epochMillis: Long): String = LocalContext.current.formatTimeOfDay(epochMillis = epochMillis)

@Composable
internal fun formatDuration(minutes: Long): String = LocalContext.current.formatDuration(minutes = minutes)
