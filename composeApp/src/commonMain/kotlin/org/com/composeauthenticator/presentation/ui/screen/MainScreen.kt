package org.com.composeauthenticator.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.com.composeauthenticator.data.model.UserAccount
import org.com.composeauthenticator.presentation.viewmodel.MainViewModel
import org.com.composeauthenticator.presentation.ui.component.AccountCard
import org.com.composeauthenticator.utils.CryptoUtils
import org.com.composeauthenticator.platform.KeyService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    keyService: KeyService,
    onNavigateToAddAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Compose Authenticator",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
    ) {
        paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
        {

            Column(
                modifier = modifier
                    .fillMaxSize()
            )
            {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.accounts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No accounts added yet",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap the + button to add your first account",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.accounts) { account ->
                            AccountCard(
                                account = account,
                                keyService = keyService,
                                onDelete = { viewModel.deleteAccount(account) }
                            )
                        }
                    }
                }


            }

            FloatingActionButton(
                onClick = onNavigateToAddAccount,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        }
    }
}

@Composable
private fun AccountCard(
    account: UserAccount,
    keyService: KeyService,
    onDelete: () -> Unit
) {
    var decryptedSecret by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(account.sharedKey) {
        try {
            decryptedSecret = CryptoUtils.decrypt(account.sharedKey, keyService.getKey())
            isLoading = false
        } catch (e: Exception) {
            decryptedSecret = null
            isLoading = false
        }
    }
    
    when {
        isLoading -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
        decryptedSecret != null -> {
            AccountCard(
                account = account,
                decryptedSecret = decryptedSecret!!,
                onDelete = onDelete
            )
        }
        else -> {
            // Show error card for failed decryption
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = account.name ?: "Unknown Account",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Failed to decrypt account data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}