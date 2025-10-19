package org.com.composeauthenticator.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): SavitDatabase {
    val dbFilePath = NSHomeDirectory() + "/Documents/SavitDatabase.db"
    return Room.databaseBuilder<SavitDatabase>(
        name = dbFilePath,
    )
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
}