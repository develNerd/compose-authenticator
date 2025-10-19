package org.com.composeauthenticator.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    onQRCodeScanned: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf<String>("") }

    LaunchedEffect(
        key1 = code
    ) {
        if (code.isNotEmpty()) {
            onQRCodeScanned(code)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { flashlightOn = !flashlightOn }) {
                        Icon(
                            imageVector = if (flashlightOn) Icons.Default.Lightbulb else Icons.Default.FlashAuto,
                            contentDescription = if (flashlightOn) "Turn off flashlight" else "Turn on flashlight"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            QrScanner(
                modifier = Modifier.fillMaxSize(),
                flashlightOn = flashlightOn,
                cameraLens = CameraLens.Back,
                openImagePicker = openImagePicker,
                onCompletion = { qrCode ->
                    code = qrCode
                },
                imagePickerHandler = { isOpen ->
                    openImagePicker = isOpen
                },
                onFailure = { error ->
                    // Handle scanning failure
                    println("QR Scanning failed: $error")
                },
                overlayShape = OverlayShape.Square,
                overlayColor = Color(0x88000000),
                overlayBorderColor = Color.White,
                zoomLevel = 1f,
                maxZoomLevel = 3f
            )
            
            // Instructions overlay
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Position the QR code within the frame",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { openImagePicker = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Choose from Gallery")
                    }
                }
            }
        }
    }
}