package com.seijind.ninetolife.di

import androidx.room.Room
import com.seijind.ninetolife.data.settings.DataStoreFileName
import com.seijind.ninetolife.data.settings.createDataStore
import com.seijind.ninetolife.data.shift.db.AppDatabase
import com.seijind.ninetolife.data.shift.db.DatabaseFileName
import com.seijind.ninetolife.data.shift.db.buildAppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual fun platformModule(): Module = module {
    single { createDataStore(path = documentPath(fileName = DataStoreFileName)) }
    single { Room.databaseBuilder<AppDatabase>(name = documentPath(fileName = DatabaseFileName)).buildAppDatabase() }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentPath(fileName: String): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val path = requireNotNull(value = directory?.path) { "Documents directory is unavailable" }
    return "$path/$fileName"
}
