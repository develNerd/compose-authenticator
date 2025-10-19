package org.com.composeauthenticator.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.com.composeauthenticator.data.model.UserAccount
import org.com.composeauthenticator.utils.TOTPGenerator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AccountCard(
    account: UserAccount,
    decryptedSecret: String,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
        // Real-time TOTP and remaining time states
    var totp by remember { mutableStateOf("------") }
    var remainingTime by remember { mutableLongStateOf(30L) }
    
    // Continuous progress animation
    var currentTimeMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    
    // Real-time updates
    LaunchedEffect(Unit) {
        while (true) {
            val now = Clock.System.now().toEpochMilliseconds()
            currentTimeMillis = now
            
            val currentTimeSeconds = now / 1000
            val newRemainingTime = TOTPGenerator.getRemainingTime(currentTimeSeconds)
            
            try {
                val newTotp = TOTPGenerator.generateTOTP(decryptedSecret, currentTimeSeconds)
                // Only update TOTP if it actually changed to avoid unnecessary recomposition
                if (totp != newTotp) {
                    totp = newTotp
                }
            } catch (e: Exception) {
                totp = "ERROR"
            }
            
            remainingTime = newRemainingTime
            delay(100) // Update every 100ms for smooth animation
        }
    }
    
    // Smooth progress calculation using milliseconds for continuous animation
    val progressMultiplyingFactor = remember(currentTimeMillis) {
        derivedStateOf {
            val currentSeconds = currentTimeMillis / 1000.0
            val remainder = currentSeconds % 30.0
            val progress = (30.0 - remainder) / 30.0
            progress.toFloat().coerceIn(0f, 1f)
        }
    }.value
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with account info and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = account.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (account.issuer != null) {
                        Text(
                            text = account.issuer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(totp))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code"
                        )
                    }
                    
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Account",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // TOTP display with real-time updates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = totp,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                
                // Custom circular progress bar
                Box(
                    modifier = Modifier.size(70.dp)
                ) {
                    CustomProgressBar(
                        progressMultiplyingFactor = progressMultiplyingFactor,
                        secondsRemaining = remainingTime.toString()
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text("Are you sure you want to delete the account for ${account.name ?: "this account"}? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}