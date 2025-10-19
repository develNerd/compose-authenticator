package org.com.composeauthenticator.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.com.composeauthenticator.presentation.viewmodel.AddAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    viewModel: AddAccountViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQRScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Add Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding() + 16.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {

            if (!uiState.isManualEntry) {
                // QR Code scanning section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Scan QR Code",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Point your camera at the QR code provided by the service",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToQRScanner,
                            enabled = !uiState.isLoading
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open QR Scanner")
                        }
                    }
                }
            }

            // Manual entry toggle
            TextButton(
                onClick = { viewModel.toggleManualEntry() }
            ) {
                Text(
                    if (uiState.isManualEntry) "Use QR Code Scanner" else "Enter Details Manually"
                )
            }

            if (uiState.isManualEntry || uiState.accountName.isNotEmpty()) {
                // Manual entry form
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Account Details",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        OutlinedTextField(
                            value = uiState.accountName,
                            onValueChange = { viewModel.updateAccountName(it) },
                            label = { Text("Account Name") },
                            placeholder = { Text("e.g., john@example.com") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.issuer,
                            onValueChange = { viewModel.updateIssuer(it) },
                            label = { Text("Issuer (Optional)") },
                            placeholder = { Text("e.g., Google, GitHub") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.secret,
                            onValueChange = { viewModel.updateSecret(it) },
                            label = { Text("Secret Key") },
                            placeholder = { Text("Enter the secret key") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                viewModel.saveAccount().also {
                                    onNavigateBack()
                                }

                                      },
                            enabled = !uiState.isLoading &&
                                    uiState.accountName.isNotBlank() &&
                                    uiState.secret.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Add Account")
                            }
                        }
                    }
                }
            }

            // Error handling
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    

}