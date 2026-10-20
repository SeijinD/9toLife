package com.seijind.ninetolife.ui.scanner.navigation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seijind.ninetolife.ui.app.navigation.ScannerKey
import com.seijind.ninetolife.ui.common.ObserveAsEvents
import com.seijind.ninetolife.ui.common.haptics.LocalAppHaptics
import com.seijind.ninetolife.ui.scanner.composable.QrCameraPreview
import com.seijind.ninetolife.ui.scanner.composable.ScannerContent
import com.seijind.ninetolife.ui.scanner.model.CameraPermission
import com.seijind.ninetolife.ui.scanner.model.ScannerActions
import com.seijind.ninetolife.ui.scanner.model.ScannerEvent
import com.seijind.ninetolife.ui.scanner.viewmodel.ScannerViewModel
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<NavKey>.scannerEntry(onClose: () -> Unit) {
    entry<ScannerKey> {
        val viewModel: ScannerViewModel = koinViewModel()
        val haptics = LocalAppHaptics.current
        val context = LocalContext.current
        val activity = LocalActivity.current
        var hasAskedPermission by rememberSaveable { mutableStateOf(value = false) }
        var permission by remember { mutableStateOf(value = context.cameraPermission(activity = activity, hasAsked = hasAskedPermission)) }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            hasAskedPermission = true
            permission = context.cameraPermission(activity = activity, hasAsked = true)
        }

        // Asks right away the first time; the in-screen prompt is the fallback.
        LaunchedEffect(Unit) {
            if (permission == CameraPermission.REQUESTABLE && !hasAskedPermission) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // Re-checks after a trip to the system settings.
        LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
            permission = context.cameraPermission(activity = activity, hasAsked = hasAskedPermission)
        }

        ObserveAsEvents(flow = viewModel.events) { event ->
            when (event) {
                ScannerEvent.QrSaved -> {
                    haptics.confirm()
                    onClose()
                }
            }
        }

        val actions = ScannerActions(
            onClose = onClose,
            onGrantPermissionClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onOpenSettingsClick = { context.openAppSettings() },
        )
        ScannerContent(
            permission = permission,
            actions = actions,
            cameraPreview = {
                QrCameraPreview(onQrDetected = viewModel::onQrScanned, modifier = Modifier.fillMaxSize())
            },
        )
    }
}

private fun Context.cameraPermission(activity: Activity?, hasAsked: Boolean): CameraPermission = when {
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> CameraPermission.GRANTED
    // After a denial without rationale, the system dialog no longer shows; only the settings can help.
    hasAsked && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) -> CameraPermission.BLOCKED
    else -> CameraPermission.REQUESTABLE
}

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
