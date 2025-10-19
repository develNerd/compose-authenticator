package org.com.composeauthenticator.data.repository

import kotlinx.coroutines.flow.Flow
import org.com.composeauthenticator.data.dao.UserAccountDao
import org.com.composeauthenticator.data.model.UserAccount

class UserAccountRepositoryImpl(
    private val userAccountDao: UserAccountDao
) : UserAccountRepository {
    
    override suspend fun insertUserAccount(userAccount: UserAccount) {
        userAccountDao.insertUserData(userAccount)
    }
    
    override suspend fun updateUserAccount(userAccount: UserAccount) {
        userAccountDao.updateUserAccountData(userAccount)
    }
    
    override suspend fun deleteUserAccount(userAccount: UserAccount) {
        userAccountDao.deleteUserAccount(userAccount)
    }
    
    override suspend fun deleteUserAccount(sharedKey: String) {
        userAccountDao.deleteAccount(sharedKey)
    }
    
    override suspend fun getUserAccount(sharedKey: String): UserAccount? {
        return userAccountDao.loadUserAccount(sharedKey)
    }
    
    override suspend fun getUserAccount(id: Int): UserAccount? {
        return userAccountDao.loadUserAccountData(id)
    }
    
    override fun getAllUserAccounts(): Flow<List<UserAccount>> {
        return userAccountDao.loadUsers()
    }
    
    override suspend fun getUserCount(): Int {
        return userAccountDao.getUserCount()
    }
}