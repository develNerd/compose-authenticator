package org.com.composeauthenticator.di

import org.com.composeauthenticator.data.repository.UserAccountRepository
import org.com.composeauthenticator.data.repository.UserAccountRepositoryImpl
import org.com.composeauthenticator.presentation.viewmodel.MainViewModel
import org.com.composeauthenticator.presentation.viewmodel.AddAccountViewModel
import org.com.composeauthenticator.presentation.viewmodel.TOTPViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single<UserAccountRepository> { 
        UserAccountRepositoryImpl(get()) 
    }
    
    // ViewModels
    viewModel { MainViewModel(get()) }
    viewModel { AddAccountViewModel(get(), get( ),get()) }
    viewModel { TOTPViewModel() }
}