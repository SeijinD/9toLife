package com.seijind.ninetolife.ui.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.seijind.ninetolife.ui.common.navigation.BottomSheetSceneStrategy
import com.seijind.ninetolife.ui.history.navigation.historyEntry
import com.seijind.ninetolife.ui.home.navigation.homeEntry
import com.seijind.ninetolife.ui.scanner.navigation.scannerEntry
import com.seijind.ninetolife.ui.settings.navigation.settingsEntry
import com.seijind.ninetolife.ui.splash.navigation.splashEntry

private const val TransitionMillis = 280

@Composable
internal fun AppNavDisplay(
    isReady: Boolean,
    seriousMode: Boolean,
    quickQrRequest: () -> Long,
) {
    val backStack = rememberNavBackStack(SplashKey)
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    // NavDisplay keeps each NavEntry until the back stack changes, so entries read state through lambdas.
    val currentIsReady by rememberUpdatedState(newValue = isReady)
    val currentSeriousMode by rememberUpdatedState(newValue = seriousMode)

    // The QR shortcut skips the splash and closes whatever is on top of Home.
    val request = quickQrRequest()
    LaunchedEffect(request) {
        if (request == 0L) return@LaunchedEffect
        if (HomeKey !in backStack) backStack.add(element = HomeKey)
        backStack.removeAll { it != HomeKey }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(bottomSheetStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = { fadeTransition() },
        popTransitionSpec = { fadeTransition() },
        predictivePopTransitionSpec = { fadeTransition() },
        entryProvider = entryProvider {
            splashEntry(
                isReady = { currentIsReady },
                seriousMode = { currentSeriousMode },
                onFinished = {
                    backStack.add(element = HomeKey)
                    backStack.remove(element = SplashKey)
                },
            )
            homeEntry(
                quickQrRequest = quickQrRequest,
                onOpenScanner = { backStack.add(element = ScannerKey) },
                onOpenSettings = { backStack.add(element = SettingsKey) },
                onOpenHistory = { backStack.add(element = HistoryKey) },
            )
            scannerEntry(onClose = { backStack.removeLastOrNull() })
            settingsEntry(
                onOpenScanner = {
                    backStack.removeLastOrNull()
                    backStack.add(element = ScannerKey)
                },
            )
            historyEntry(onBack = { backStack.removeLastOrNull() })
        },
    )
}

private fun fadeTransition(): ContentTransform =
    fadeIn(animationSpec = tween(durationMillis = TransitionMillis)) togetherWith fadeOut(animationSpec = tween(durationMillis = TransitionMillis))
