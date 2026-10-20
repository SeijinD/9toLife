package com.seijind.ninetolife.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.seijind.ninetolife.di.AppScope
import com.seijind.ninetolife.usecase.shift.RestoreShiftAlertsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Alarms and notifications do not survive a reboot or an app update; this puts them back. */
internal class RestoreAlertsReceiver : BroadcastReceiver(), KoinComponent {

    private val restoreShiftAlerts: RestoreShiftAlertsUseCase by inject()
    private val appScope: CoroutineScope by inject(qualifier = AppScope)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val pendingResult = goAsync()
        appScope.launch {
            try {
                restoreShiftAlerts()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
