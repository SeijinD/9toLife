package com.seijind.ninetolife.di

import android.content.Context
import androidx.room.Room
import com.seijind.ninetolife.data.settings.DataStoreFileName
import com.seijind.ninetolife.data.settings.createDataStore
import com.seijind.ninetolife.data.shift.db.AppDatabase
import com.seijind.ninetolife.data.shift.db.DatabaseFileName
import com.seijind.ninetolife.data.shift.db.buildAppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/** Expects the app to register its Context (Koin's androidContext) and a ShiftAlerts implementation. */
internal actual fun platformModule(): Module = module {
    single {
        createDataStore(path = get<Context>().filesDir.resolve(DataStoreFileName).absolutePath)
    }
    single {
        val context = get<Context>()
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(DatabaseFileName).absolutePath,
        ).buildAppDatabase()
    }
}
