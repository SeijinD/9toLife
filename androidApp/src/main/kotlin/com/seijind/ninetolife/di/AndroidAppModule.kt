package com.seijind.ninetolife.di

import com.seijind.ninetolife.alerts.ShiftAlertsImpl
import com.seijind.ninetolife.data.background.BackgroundImageImporter
import com.seijind.ninetolife.domain.shift.ShiftAlerts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal val AppScope = named(name = "appScope")

internal val androidAppModule = module {
    // For work that must outlive a screen or a broadcast receiver.
    single<CoroutineScope>(qualifier = AppScope) { CoroutineScope(context = SupervisorJob() + Dispatchers.Default) }
    singleOf(::ShiftAlertsImpl) bind ShiftAlerts::class
    singleOf(::BackgroundImageImporter)
}
