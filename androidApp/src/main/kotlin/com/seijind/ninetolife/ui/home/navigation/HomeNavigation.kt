package com.seijind.ninetolife.ui.home.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seijind.ninetolife.R
import com.seijind.ninetolife.data.background.BackgroundImageImporter
import com.seijind.ninetolife.format.formatDuration
import com.seijind.ninetolife.ui.app.navigation.HomeKey
import com.seijind.ninetolife.ui.common.ObserveAsEvents
import com.seijind.ninetolife.ui.common.haptics.LocalAppHaptics
import com.seijind.ninetolife.ui.common.showUndoSnackbar
import com.seijind.ninetolife.ui.home.composable.FreedomCelebration
import com.seijind.ninetolife.ui.home.composable.HomeContent
import com.seijind.ninetolife.ui.home.model.HomeActions
import com.seijind.ninetolife.ui.home.model.HomeEvent
import com.seijind.ninetolife.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<NavKey>.homeEntry(
    quickQrRequest: () -> Long,
    onOpenScanner: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    entry<HomeKey> {
        val viewModel: HomeViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val haptics = LocalAppHaptics.current
        val context = LocalContext.current
        val resources = LocalResources.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val backgroundImporter: BackgroundImageImporter = koinInject()
        // Old photos go only once the saved setting no longer points to them.
        LaunchedEffect(key1 = uiState.isLoading, key2 = uiState.backgroundImagePath) {
            if (!uiState.isLoading) backgroundImporter.deleteAllExcept(path = uiState.backgroundImagePath)
        }
        var showFreedom by rememberSaveable { mutableStateOf(value = false) }
        val notificationPermission = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {},
        )
        // Asked when the QR closes, so the system dialog never covers the QR at the reader.
        var wasQrExpanded by remember { mutableStateOf(value = uiState.isQrExpanded) }
        LaunchedEffect(key1 = uiState.isQrExpanded) {
            val qrJustClosed = wasQrExpanded && !uiState.isQrExpanded
            wasQrExpanded = uiState.isQrExpanded
            if (!qrJustClosed || uiState.shift == null) return@LaunchedEffect
            val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            if (!needsPermission) return@LaunchedEffect
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Saved, so coming back to Home (or a recreated activity) does not clock in or out again.
        var handledQuickQrRequest by rememberSaveable { mutableLongStateOf(value = 0L) }
        val request = quickQrRequest()
        LaunchedEffect(key1 = request) {
            if (request <= handledQuickQrRequest) return@LaunchedEffect
            handledQuickQrRequest = request
            viewModel.onQuickQrRequest()
        }

        // Only the newest undo snackbar owns the pending undo; one that was replaced must not expire it.
        var undoSnackbarCount by remember { mutableIntStateOf(value = 0) }

        fun showUndoSnackbar(message: String) {
            val snackbarNumber = ++undoSnackbarCount
            scope.launch {
                val result = snackbarHostState.showUndoSnackbar(
                    message = message,
                    undoLabel = resources.getString(R.string.common_undo),
                )
                when {
                    result == SnackbarResult.ActionPerformed -> viewModel.onUndoClick()
                    snackbarNumber == undoSnackbarCount -> viewModel.onUndoExpired()
                }
            }
        }

        fun showSnackbar(message: String) {
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(message = message)
            }
        }

        ObserveAsEvents(flow = viewModel.events) { event ->
            val serious = uiState.seriousMode
            when (event) {
                HomeEvent.OpenScanner -> onOpenScanner()
                HomeEvent.ShiftStarted -> {
                    haptics.confirm()
                    showUndoSnackbar(
                        message = resources.getString(
                            if (serious) R.string.home_shift_started_serious else R.string.home_shift_started_fun,
                        ),
                    )
                }
                is HomeEvent.ShiftEnded -> {
                    haptics.confirm()
                    showUndoSnackbar(
                        message = resources.getString(
                            if (serious) R.string.home_shift_ended_serious else R.string.home_shift_ended_fun,
                            context.formatDuration(minutes = event.workedMinutes),
                        ),
                    )
                }
                HomeEvent.UndoDone -> {
                    haptics.reject()
                    showSnackbar(message = resources.getString(R.string.home_undo_done))
                }
                HomeEvent.Freedom -> {
                    haptics.celebrate()
                    if (serious) {
                        showSnackbar(message = resources.getString(R.string.home_freedom_serious))
                    } else {
                        showFreedom = true
                    }
                }
            }
        }

        BackHandler(enabled = uiState.isQrExpanded, onBack = viewModel::onQrDismiss)

        Box(modifier = Modifier.fillMaxSize()) {
            HomeContent(
                uiState = uiState,
                backgroundImage = rememberBackgroundImage(path = uiState.backgroundImagePath),
                actions = HomeActions(
                    onAddQrClick = {
                        haptics.confirm()
                        viewModel.onAddQrClick()
                    },
                    onQrClick = viewModel::onQrClick,
                    onQrDismiss = viewModel::onQrDismiss,
                    onSettingsClick = onOpenSettings,
                    onHistoryClick = onOpenHistory,
                ),
            )
            if (showFreedom) FreedomCelebration(onFinished = { showFreedom = false })
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .safeDrawingPadding(),
            )
        }
    }
}

/** Decodes the background photo off the main thread; null while loading or when none is set. */
@Composable
private fun rememberBackgroundImage(path: String?): ImageBitmap? {
    val image by produceState<ImageBitmap?>(initialValue = null, path) {
        value = path?.let { withContext(context = Dispatchers.IO) { BitmapFactory.decodeFile(it)?.asImageBitmap() } }
    }
    return image
}
