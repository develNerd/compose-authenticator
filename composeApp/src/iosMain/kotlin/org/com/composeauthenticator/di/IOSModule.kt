package org.com.composeauthenticator.di

import org.com.composeauthenticator.data.database.SavitDatabase
import org.com.composeauthenticator.data.database.getDatabaseBuilder
import org.com.composeauthenticator.platform.*
import org.com.composeauthenticator.platform.ios.*
import org.koin.dsl.module
import eu.anifantakis.lib.ksafe.KSafe

val iosModule = module {
    single<SavitDatabase> { 
        getDatabaseBuilder() 
    }
    
    single { get<SavitDatabase>().getUserAccountDao() }
    single { KSafe() }

    single<KeyService> {
        KSafeKeyService(get())
    }

    
    single<CameraPermissionService> { 
        IOSCameraPermissionService() 
    }
    
    single<ClipboardService> {
        IOSClipboardService()
    }
}