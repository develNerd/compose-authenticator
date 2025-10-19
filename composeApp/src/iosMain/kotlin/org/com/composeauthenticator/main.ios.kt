package org.com.composeauthenticator

import androidx.compose.ui.window.ComposeUIViewController
import org.com.composeauthenticator.di.commonModule
import org.com.composeauthenticator.di.iosModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    // Initialize Koin for iOS
    startKoin {
        modules(
            commonModule,
            iosModule
        )
    }
    
    App()
}