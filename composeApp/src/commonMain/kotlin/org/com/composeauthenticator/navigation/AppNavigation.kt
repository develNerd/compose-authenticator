package org.com.composeauthenticator.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import org.com.composeauthenticator.data.repository.UserAccountRepository
import org.com.composeauthenticator.platform.*
import org.koin.compose.koinInject

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddAccount : Screen("add_account")
    object QRScanner : Screen("qr_scanner")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Inject dependencies using Koin
    val repository: UserAccountRepository = koinInject()
    val qrCodeScannerService: QRCodeScannerService = koinInject()
    val cameraPermissionService: CameraPermissionService = koinInject()
    val keyService: KeyService = koinInject()

    NavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = Screen.Main.route
    ) {
        composable(
            route = Screen.Main.route
        ) {
            val mainViewModel = remember {
                MainViewModel(
                    repository = repository
                )
            }

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
            val addAccountViewModel = remember {
                AddAccountViewModel(
                    repository = repository,
                    qrCodeScannerService = qrCodeScannerService,
                    cameraPermissionService = cameraPermissionService,
                    keyService = keyService
                )
            }



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
                    // Navigate back with result
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