package com.seijind.ninetolife

import android.app.Application
import com.seijind.ninetolife.alerts.ShiftNotifications
import com.seijind.ninetolife.di.androidAppModule
import com.seijind.ninetolife.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NineToLifeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(androidContext = this@NineToLifeApp)
            modules(sharedModules() + androidAppModule)
        }
        ShiftNotifications.createChannels(context = this)
    }
}
