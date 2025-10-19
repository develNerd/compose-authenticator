package org.com.composeauthenticator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.com.composeauthenticator.utils.TOTPGenerator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class TOTPState(
    val totpCode: String = "------",
    val remainingTime: Long = 30L,
    val progressMultiplyingFactor: Float = 1.0f, // Smooth progress for animation
    val isError: Boolean = false
)

class TOTPViewModel : ViewModel() {
    
    private val _totpState = MutableStateFlow(TOTPState())
    val totpState: StateFlow<TOTPState> = _totpState.asStateFlow()
    
    @OptIn(ExperimentalTime::class)
    fun startTOTPUpdates(decryptedSecret: String) {
        viewModelScope.launch {
            while (true) {
                try {
                    val now = Clock.System.now().toEpochMilliseconds()
                    val currentTimeSeconds = now / 1000
                    
                    val newRemainingTime = TOTPGenerator.getRemainingTime(currentTimeSeconds)
                    val newTotp = TOTPGenerator.generateTOTP(decryptedSecret, currentTimeSeconds)
                    
                    // Calculate smooth progress using milliseconds for continuous animation
                    val currentSeconds = now / 1000.0
                    val remainder = currentSeconds % 30.0
                    val smoothProgress = (30.0 - remainder) / 30.0
                    
                    _totpState.value = TOTPState(
                        totpCode = newTotp,
                        remainingTime = newRemainingTime,
                        progressMultiplyingFactor = smoothProgress.toFloat().coerceIn(0f, 1f),
                        isError = false
                    )
                    
                } catch (e: Exception) {
                    _totpState.value = _totpState.value.copy(
                        totpCode = "ERROR",
                        isError = true
                    )
                }
                
                delay(100) // Update every 100ms for smooth animation
            }
        }
    }
}