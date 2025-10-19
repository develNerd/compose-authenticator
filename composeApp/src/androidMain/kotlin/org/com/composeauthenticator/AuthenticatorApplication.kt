package org.com.composeauthenticator

import android.app.Application
import org.com.composeauthenticator.di.androidModule
import org.com.composeauthenticator.di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AuthenticatorApplication : Application() {
    
    companion object {
        lateinit var INSTANCE: AuthenticatorApplication
    }
    
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        // Initialize Koin with Android context
        startKoin {
            androidContext(applicationContext)
            modules(
                commonModule,
                androidModule
            )
        }
        
        // QRKit initialization will be handled automatically by the library
        // No manual AppContext setup needed
    }
}