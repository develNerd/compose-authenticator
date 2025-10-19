package org.com.composeauthenticator.di

import android.content.Context
import org.com.composeauthenticator.data.database.SavitDatabase
import org.com.composeauthenticator.data.database.getDatabaseBuilder
import org.com.composeauthenticator.platform.*
import org.com.composeauthenticator.platform.android.*
import org.koin.dsl.module
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.android.ext.koin.androidApplication

val androidModule = module {
    single<SavitDatabase> { 
        getDatabaseBuilder() 
    }

    single { get<SavitDatabase>().getUserAccountDao() }
    single { KSafe(androidApplication()) }

    single<KeyService> { 
        KSafeKeyService(get())
    }

    single<TimeService> { 
        AndroidTimeService() 
    }
    
    single<CameraPermissionService> { 
        AndroidCameraPermissionService(get<Context>()) 
    }
    
    single<QRCodeScannerService> { 
        AndroidQRCodeScannerService() 
    }
}