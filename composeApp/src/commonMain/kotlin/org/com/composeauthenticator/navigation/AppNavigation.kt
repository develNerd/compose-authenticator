package org.com.composeauthenticator.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.com.composeauthenticator.presentation.ui.screen.MainScreen
import org.com.composeauthenticator.presentation.ui.screen.AddAccountScreen
import org.com.composeauthenticator.presentation.ui.screen.QRScannerScreen
import org.com.composeauthenticator.presentation.viewmodel.MainViewModel
import org.com.composeauthenticator.presentation.viewmodel.AddAccountViewModel
import org.com.composeauthenticator.platform.KeyService
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.rememberCoroutineScope

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddAccount : Screen("add_account")
    object QRScanner : Screen("qr_scanner")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Only inject services that are directly used in this composable
    val keyService: KeyService = koinInject()
    val scope = rememberCoroutineScope()


    NavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = Screen.Main.route
    ) {
        composable(
            route = Screen.Main.route
        ) {
            val mainViewModel: MainViewModel = koinViewModel()

            MainScreen(
                viewModel = mainViewModel,
                keyService = keyService,
                onNavigateToAddAccount = {
                    navController.navigate(Screen.AddAccount.route)
                }
            )
        }

        composable(
            route = Screen.AddAccount.route
        ) { backStackEntry ->
            val addAccountViewModel: AddAccountViewModel = koinViewModel()

            // Handle QR scan result
            val qrResult = backStackEntry.savedStateHandle.get<String>("qr_result")
            LaunchedEffect(qrResult) {
                qrResult?.let {
                    addAccountViewModel.handleQRCodeResult(it)
                    backStackEntry.savedStateHandle.remove<String>("qr_result")
                }
            }

            AddAccountScreen(
                viewModel = addAccountViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQRScanner = {
                    navController.navigate(Screen.QRScanner.route)
                }
            )
        }

        composable(
            route = Screen.QRScanner.route
        ) {

            val scope = rememberCoroutineScope()

            QRScannerScreen(
                onQRCodeScanned = { qrCode ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("qr_result", qrCode)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}