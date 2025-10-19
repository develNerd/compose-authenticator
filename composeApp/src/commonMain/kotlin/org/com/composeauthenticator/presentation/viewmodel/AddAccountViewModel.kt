package org.com.composeauthenticator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.com.composeauthenticator.data.repository.UserAccountRepository
import org.com.composeauthenticator.data.model.UserAccount
import org.com.composeauthenticator.platform.CameraPermissionService
import org.com.composeauthenticator.platform.KeyService
import org.com.composeauthenticator.utils.QRCodeParser
import org.com.composeauthenticator.utils.CryptoUtils

data class AddAccountUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isScanning: Boolean = false,
    val hasPermission: Boolean = false,
    val accountName: String = "",
    val issuer: String = "",
    val secret: String = "",
    val isManualEntry: Boolean = false
)

class AddAccountViewModel(
    private val repository: UserAccountRepository,
    private val cameraPermissionService: CameraPermissionService,
    private val keyService: KeyService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddAccountUiState())
    val uiState: StateFlow<AddAccountUiState> = _uiState.asStateFlow()
    
    init {
        checkCameraPermission()
    }
    
    private fun checkCameraPermission() {
        viewModelScope.launch {
            val hasPermission = cameraPermissionService.hasPermission()
            _uiState.value = _uiState.value.copy(hasPermission = hasPermission)
        }
    }
    
    fun requestCameraPermission() {
        viewModelScope.launch {
            val granted = cameraPermissionService.requestPermission()
            _uiState.value = _uiState.value.copy(hasPermission = granted)
        }
    }
    

    
    fun handleQRCodeResult(qrCode: String) {
        val otpAuthUri = QRCodeParser.parseOTPAuthUri(qrCode)
        if (otpAuthUri != null) {
            _uiState.value = _uiState.value.copy(
                accountName = otpAuthUri.accountName ?: "",
                issuer = otpAuthUri.issuer ?: "",
                secret = otpAuthUri.secret,
                isScanning = false,
                error = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                error = "Invalid QR code format",
                isScanning = false
            )
        }
    }
    
    fun updateAccountName(name: String) {
        _uiState.value = _uiState.value.copy(accountName = name)
    }
    
    fun updateIssuer(issuer: String) {
        _uiState.value = _uiState.value.copy(issuer = issuer)
    }
    
    fun updateSecret(secret: String) {
        _uiState.value = _uiState.value.copy(secret = secret)
    }
    
    fun toggleManualEntry() {
        _uiState.value = _uiState.value.copy(
            isManualEntry = !_uiState.value.isManualEntry,
            error = null
        )
    }
    
    fun saveAccount() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                if (state.accountName.isBlank() || state.secret.isBlank()) {
                    _uiState.value = state.copy(error = "Account name and secret are required")
                    return@launch
                }
                
                _uiState.value = state.copy(isLoading = true, error = null)
                
                val encryptedSecret = CryptoUtils.encrypt(state.secret, keyService.getKey())
                
                val account = UserAccount(
                    name = state.accountName,
                    issuer = state.issuer.ifBlank { null },
                    sharedKey = encryptedSecret,
                    image = 0, // Default image
                    code = null
                )
                
                repository.insertUserAccount(account)
                
                _uiState.value = AddAccountUiState() // Reset state
                
            } catch (e: Exception) {
                Logger.e {  "Error saving account: ${e.message}" }
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to save account",
                    isLoading = false
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}