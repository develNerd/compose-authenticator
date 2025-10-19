package org.com.composeauthenticator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.com.composeauthenticator.data.repository.UserAccountRepository
import org.com.composeauthenticator.data.model.UserAccount
import org.com.composeauthenticator.utils.TOTPGenerator

data class MainUiState(
    val accounts: List<UserAccount> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    private val repository: UserAccountRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadAccounts()
    }
    
    private fun loadAccounts() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getAllUserAccounts().collect { accounts ->
                    _uiState.value = _uiState.value.copy(
                        accounts = accounts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
    fun deleteAccount(account: UserAccount) {
        viewModelScope.launch {
            try {
                repository.deleteUserAccount(account)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

}