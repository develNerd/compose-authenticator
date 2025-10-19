package org.com.composeauthenticator.data.database

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

actual fun getDatabaseBuilder(): SavitDatabase {
    val context: Context by inject(Context::class.java)


    val dbFilePath = context.getDatabasePath("SavitDatabase.db").absolutePath
    return Room.databaseBuilder<SavitDatabase>(
        context = context.applicationContext,
        name = dbFilePath,
    )
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
}
