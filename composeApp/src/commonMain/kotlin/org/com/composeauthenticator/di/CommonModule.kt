package org.com.composeauthenticator.di

import org.com.composeauthenticator.data.repository.UserAccountRepository
import org.com.composeauthenticator.data.repository.UserAccountRepositoryImpl
import org.koin.dsl.module

val commonModule = module {
    single<UserAccountRepository> { 
        UserAccountRepositoryImpl(get()) 
    }

}