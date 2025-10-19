package org.com.composeauthenticator

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        


        setContent {
            App(
                platformModules = listOf(
                    org.com.composeauthenticator.di.androidModule
                )
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}