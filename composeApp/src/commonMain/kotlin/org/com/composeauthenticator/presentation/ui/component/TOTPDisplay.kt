package org.com.composeauthenticator.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.com.composeauthenticator.utils.TOTPGenerator

@Composable
fun TOTPDisplay(
    secret: String,
    modifier: Modifier = Modifier
) {
    var totpCode by remember { mutableStateOf("") }
    var remainingTime by remember { mutableLongStateOf(30L) }
    var isError by remember { mutableStateOf(false) }
    
    // Update TOTP code and remaining time every second
    LaunchedEffect(secret) {
        while (true) {
            try {
                totpCode = TOTPGenerator.generateTOTP(secret)
                remainingTime = TOTPGenerator.getRemainingTime()
                isError = false
            } catch (e: Exception) {
                totpCode = "ERROR"
                isError = true
            }
            delay(1000L)
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOTP Code Display
            Text(
                text = totpCode,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress indicator and remaining time
            if (!isError) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { remainingTime / 30f },
                        modifier = Modifier.weight(1f),
                        color = when {
                            remainingTime <= 5 -> Color.Red
                            remainingTime <= 10 -> Color(0xFFFF9800) // Orange
                            else -> MaterialTheme.colorScheme.primary
                        },
                    )
                    
                    Text(
                        text = "${remainingTime}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

