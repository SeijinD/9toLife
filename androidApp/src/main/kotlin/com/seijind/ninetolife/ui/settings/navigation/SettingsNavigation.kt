package com.seijind.ninetolife.ui.settings.navigation

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seijind.ninetolife.R
import com.seijind.ninetolife.data.background.BackgroundImageImporter
import com.seijind.ninetolife.ui.app.navigation.SettingsKey
import com.seijind.ninetolife.ui.common.ObserveAsEvents
import com.seijind.ninetolife.ui.common.haptics.LocalAppHaptics
import com.seijind.ninetolife.ui.common.locale.applyAppLanguage
import com.seijind.ninetolife.ui.common.locale.currentAppLanguage
import com.seijind.ninetolife.ui.common.navigation.BottomSheetSceneStrategy
import com.seijind.ninetolife.ui.settings.composable.SettingsContent
import com.seijind.ninetolife.ui.settings.model.SettingsActions
import com.seijind.ninetolife.ui.settings.model.SettingsEvent
import com.seijind.ninetolife.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<NavKey>.settingsEntry(onOpenScanner: () -> Unit) {
    entry<SettingsKey>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
        val viewModel: SettingsViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val backgroundImporter: BackgroundImageImporter = koinInject()
        val haptics = LocalAppHaptics.current
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var language by remember { mutableStateOf(value = currentAppLanguage()) }
        var needsExactReminders by remember { mutableStateOf(value = context.needsExactAlarmPermission()) }

        LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
            needsExactReminders = context.needsExactAlarmPermission()
        }

        val photoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            // Closing the sheet mid-import is safe: the old photo stays until the new path is saved.
            scope.launch {
                val path = backgroundImporter.import(uri = uri)
                if (path != null) {
                    haptics.confirm()
                    viewModel.onBackgroundImagePicked(path = path)
                } else {
                    haptics.reject()
                    // A Toast because the sheet has no snackbar host.
                    Toast.makeText(context, R.string.settings_background_error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        ObserveAsEvents(flow = viewModel.events) { event ->
            when (event) {
                SettingsEvent.OpenScanner -> onOpenScanner()
            }
        }

        SettingsContent(
            uiState = uiState.copy(language = language),
            needsExactReminders = needsExactReminders,
            actions = SettingsActions(
                onThemeModeSelect = { mode ->
                    haptics.tick()
                    viewModel.onThemeModeSelect(mode = mode)
                },
                onLanguageSelect = { selected ->
                    haptics.tick()
                    language = selected
                    applyAppLanguage(language = selected)
                },
                onDurationDecrease = {
                    haptics.tick()
                    viewModel.onDurationDecrease()
                },
                onDurationIncrease = {
                    haptics.tick()
                    viewModel.onDurationIncrease()
                },
                onReminderAdd = { offset ->
                    haptics.tick()
                    viewModel.onReminderAdd(offsetMinutes = offset)
                },
                onReminderRemove = { offset ->
                    haptics.tick()
                    viewModel.onReminderRemove(offsetMinutes = offset)
                },
                onAllowExactRemindersClick = { context.openExactAlarmSettings() },
                onHapticsToggle = { enabled ->
                    // Played before the change so turning haptics off still gives one last tick.
                    haptics.toggle(on = enabled)
                    viewModel.onHapticsToggle(enabled = enabled)
                },
                onSeriousModeToggle = { enabled ->
                    haptics.toggle(on = enabled)
                    viewModel.onSeriousModeToggle(enabled = enabled)
                },
                onPickBackgroundClick = {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onResetBackgroundClick = viewModel::onBackgroundImageReset,
                onChangeQrClick = viewModel::onChangeQrClick,
                onDeleteQrConfirm = viewModel::onDeleteQrConfirm,
            ),
        )
    }
}

private fun Context.needsExactAlarmPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() == false

private fun Context.openExactAlarmSettings() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    startActivity(
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.fromParts("package", packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
