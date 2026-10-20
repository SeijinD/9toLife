package com.seijind.ninetolife.di

import com.seijind.ninetolife.data.settings.SettingsRepositoryImpl
import com.seijind.ninetolife.data.shift.ShiftRepositoryImpl
import com.seijind.ninetolife.data.shift.db.AppDatabase
import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.ui.app.viewmodel.AppViewModel
import com.seijind.ninetolife.ui.history.viewmodel.HistoryViewModel
import com.seijind.ninetolife.ui.home.viewmodel.HomeViewModel
import com.seijind.ninetolife.ui.scanner.viewmodel.ScannerViewModel
import com.seijind.ninetolife.ui.settings.viewmodel.SettingsViewModel
import com.seijind.ninetolife.usecase.settings.AddReminderOffsetUseCase
import com.seijind.ninetolife.usecase.settings.DeleteQrPayloadUseCase
import com.seijind.ninetolife.usecase.settings.ObserveSettingsUseCase
import com.seijind.ninetolife.usecase.settings.RemoveReminderOffsetUseCase
import com.seijind.ninetolife.usecase.settings.SaveQrPayloadUseCase
import com.seijind.ninetolife.usecase.settings.SetBackgroundImageUseCase
import com.seijind.ninetolife.usecase.settings.SetHapticsEnabledUseCase
import com.seijind.ninetolife.usecase.settings.SetLanguageUseCase
import com.seijind.ninetolife.usecase.settings.SetSeriousModeUseCase
import com.seijind.ninetolife.usecase.settings.SetThemeModeUseCase
import com.seijind.ninetolife.usecase.settings.StepShiftDurationUseCase
import com.seijind.ninetolife.usecase.shift.CancelShiftStartUseCase
import com.seijind.ninetolife.usecase.shift.DeleteShiftUseCase
import com.seijind.ninetolife.usecase.shift.EndShiftUseCase
import com.seijind.ninetolife.usecase.shift.ObserveActiveShiftUseCase
import com.seijind.ninetolife.usecase.shift.ObserveShiftTotalsUseCase
import com.seijind.ninetolife.usecase.shift.ObserveShiftsStartedBetweenUseCase
import com.seijind.ninetolife.usecase.shift.RestoreShiftAlertsUseCase
import com.seijind.ninetolife.usecase.shift.RestoreShiftUseCase
import com.seijind.ninetolife.usecase.shift.StartShiftUseCase
import com.seijind.ninetolife.usecase.shift.UndoEndShiftUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

/** Platform pieces: DataStore and database. */
internal expect fun platformModule(): Module

fun sharedModules(): List<Module> = listOf(platformModule(), dataModule, useCaseModule, viewModelModule)

private val dataModule = module {
    single<Clock> { Clock.System }
    single { get<AppDatabase>().shiftDao() }
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    singleOf(::ShiftRepositoryImpl) bind ShiftRepository::class
}

private val useCaseModule = module {
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::SaveQrPayloadUseCase)
    factoryOf(::DeleteQrPayloadUseCase)
    factoryOf(::SetThemeModeUseCase)
    factoryOf(::SetLanguageUseCase)
    factoryOf(::SetHapticsEnabledUseCase)
    factoryOf(::SetSeriousModeUseCase)
    factoryOf(::SetBackgroundImageUseCase)
    factoryOf(::StepShiftDurationUseCase)
    factoryOf(::AddReminderOffsetUseCase)
    factoryOf(::RemoveReminderOffsetUseCase)
    factoryOf(::ObserveActiveShiftUseCase)
    factoryOf(::ObserveShiftTotalsUseCase)
    factoryOf(::ObserveShiftsStartedBetweenUseCase)
    factoryOf(::StartShiftUseCase)
    factoryOf(::CancelShiftStartUseCase)
    factoryOf(::EndShiftUseCase)
    factoryOf(::UndoEndShiftUseCase)
    factoryOf(::DeleteShiftUseCase)
    factoryOf(::RestoreShiftUseCase)
    factoryOf(::RestoreShiftAlertsUseCase)
}

private val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ScannerViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::HistoryViewModel)
}
