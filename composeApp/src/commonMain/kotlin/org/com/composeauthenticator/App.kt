package org.com.composeauthenticator

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.com.composeauthenticator.navigation.AppNavigation
import org.com.composeauthenticator.ui.theme.ComposeAuthenticatorTheme
import org.koin.core.module.Module

@Composable
@Preview
fun App(
    platformModules: List<Module> = emptyList()
) {
    ComposeAuthenticatorTheme {
        AppNavigation()
    }
}