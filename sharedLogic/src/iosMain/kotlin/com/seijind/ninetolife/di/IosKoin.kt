package com.seijind.ninetolife.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.seijind.ninetolife.domain.shift.ShiftAlerts
import com.seijind.ninetolife.ui.app.viewmodel.AppViewModel
import com.seijind.ninetolife.ui.history.viewmodel.HistoryViewModel
import com.seijind.ninetolife.ui.home.viewmodel.HomeViewModel
import com.seijind.ninetolife.ui.scanner.viewmodel.ScannerViewModel
import com.seijind.ninetolife.ui.settings.viewmodel.SettingsViewModel
import com.seijind.ninetolife.usecase.shift.RestoreShiftAlertsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module

private const val ViewModelKey = "viewModel"

/** Swift entry point: call once at launch with the Swift ShiftAlerts implementation. */
fun startKoinIos(shiftAlerts: ShiftAlerts) {
    startKoin {
        modules(sharedModules() + module { single<ShiftAlerts> { shiftAlerts } })
    }
}

/**
 * iOS has no ViewModelStoreOwner: each SwiftUI screen keeps one of these and calls [clear]
 * when it goes away, which cancels the ViewModel's viewModelScope.
 */
class IosViewModelOwner<T : ViewModel>(val viewModel: T) {
    private val store = ViewModelStore().apply { put(key = ViewModelKey, viewModel = viewModel) }

    fun clear() = store.clear()
}

/** Typed factories for Swift, which cannot call Koin's reified getters. */
class IosDependencies : KoinComponent {
    fun appViewModel(): IosViewModelOwner<AppViewModel> = IosViewModelOwner(viewModel = get())
    fun homeViewModel(): IosViewModelOwner<HomeViewModel> = IosViewModelOwner(viewModel = get())
    fun scannerViewModel(): IosViewModelOwner<ScannerViewModel> = IosViewModelOwner(viewModel = get())
    fun settingsViewModel(): IosViewModelOwner<SettingsViewModel> = IosViewModelOwner(viewModel = get())
    fun historyViewModel(): IosViewModelOwner<HistoryViewModel> = IosViewModelOwner(viewModel = get())
    fun restoreShiftAlertsUseCase(): RestoreShiftAlertsUseCase = get()
}
