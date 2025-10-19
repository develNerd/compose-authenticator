package org.com.composeauthenticator.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.com.composeauthenticator.data.dao.UserAccountDao
import org.com.composeauthenticator.data.model.UserAccount

@Database(
    entities = [UserAccount::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(SavitDatabaseConstructor::class)
abstract class SavitDatabase : RoomDatabase() {
    abstract fun getUserAccountDao(): UserAccountDao
}

// Database constructor for multiplatform - Room's annotation processor will generate
// the `actual` implementations on each platform.
@Suppress("KotlinNoActualForExpected")
expect object SavitDatabaseConstructor : RoomDatabaseConstructor<SavitDatabase> {
    override fun initialize(): SavitDatabase
}

// Platform-specific database builder - returns the database directly
expect fun getDatabaseBuilder(): SavitDatabase