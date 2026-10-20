package com.seijind.ninetolife.ui.app.composable

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seijind.ninetolife.domain.settings.model.ThemeMode
import com.seijind.ninetolife.ui.app.navigation.AppNavDisplay
import com.seijind.ninetolife.ui.app.viewmodel.AppViewModel
import com.seijind.ninetolife.ui.common.haptics.LocalAppHaptics
import com.seijind.ninetolife.ui.common.haptics.rememberAppHaptics
import com.seijind.ninetolife.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun App(
    quickQrRequest: () -> Long,
    viewModel: AppViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val darkTheme = when (uiState.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    SystemBarsEffect(darkTheme = darkTheme)

    AppTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalAppHaptics provides rememberAppHaptics(enabled = uiState.hapticsEnabled)) {
            AppNavDisplay(isReady = uiState.isReady, seriousMode = uiState.seriousMode, quickQrRequest = quickQrRequest)
        }
    }
}

/** Keeps the status/navigation bar icons readable when the in-app theme differs from the system one. */
@Composable
private fun SystemBarsEffect(darkTheme: Boolean) {
    val activity = LocalActivity.current as? ComponentActivity
    LaunchedEffect(activity, darkTheme) {
        val transparent = Color.Transparent.toArgb()
        val style = SystemBarStyle.auto(lightScrim = transparent, darkScrim = transparent) { darkTheme }
        activity?.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
    }
}
