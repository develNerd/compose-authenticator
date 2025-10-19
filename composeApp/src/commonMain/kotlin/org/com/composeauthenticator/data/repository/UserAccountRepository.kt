package org.com.composeauthenticator.data.repository

import kotlinx.coroutines.flow.Flow
import org.com.composeauthenticator.data.model.UserAccount

interface UserAccountRepository {
    suspend fun insertUserAccount(userAccount: UserAccount)
    suspend fun updateUserAccount(userAccount: UserAccount)
    suspend fun deleteUserAccount(userAccount: UserAccount)
    suspend fun deleteUserAccount(sharedKey: String)
    suspend fun getUserAccount(sharedKey: String): UserAccount?
    suspend fun getUserAccount(id: Int): UserAccount?
    fun getAllUserAccounts(): Flow<List<UserAccount>>
    suspend fun getUserCount(): Int
}