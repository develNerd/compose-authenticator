package org.com.composeauthenticator.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.com.composeauthenticator.data.model.UserAccount

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(vararg userAccount: UserAccount)

    @Update
    suspend fun updateUserAccountData(vararg userAccount: UserAccount)

    @Delete
    suspend fun deleteUserAccount(vararg userAccount: UserAccount)

    @Query("SELECT * FROM user_account WHERE id = :userAccountId")
    suspend fun loadUserAccountData(userAccountId: Int): UserAccount?

    @Query("SELECT * FROM user_account WHERE sharedKey = :mySharedKey")
    suspend fun loadUserAccount(mySharedKey: String): UserAccount?

    @Query("DELETE FROM user_account WHERE sharedKey = :sharedKey")
    suspend fun deleteAccount(sharedKey: String)

    @Query("SELECT * FROM user_account")
    fun loadUsers(): Flow<List<UserAccount>>

    @Query("SELECT COUNT(*) FROM user_account")
    suspend fun getUserCount(): Int
}