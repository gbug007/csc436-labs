package com.zybooks.ai4sarapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zybooks.ai4sarapp.SarApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.zybooks.ai4sarapp.data.AuthResponse
import com.zybooks.ai4sarapp.data.SarRepository


sealed class IncidentUiState {
    data class Success(
        val response: Response<AuthResponse>
    ) : IncidentUiState()
    data class Error(
        val error: String = "error"
    ) : IncidentUiState()

    data object Opened : IncidentUiState()
    data object Loading : IncidentUiState()
}

class SarViewModel(
    private val sarRepository: SarRepository
) : ViewModel() {

    var uiState: IncidentUiState by mutableStateOf(IncidentUiState.Opened)
        private set

    // Form fields as Compose state
    var email: String by mutableStateOf("")
        private set

    var password: String by mutableStateOf("")
        private set

    var name: String by mutableStateOf("")
        private set
    var org: String by mutableStateOf("")
        private set
    var role: String by mutableStateOf("")
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SarApplication)
                SarViewModel(application.sarRepository)
            }
        }
    }

    fun logIn() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = IncidentUiState.Loading
            uiState = try {
                IncidentUiState.Success(
                    sarRepository.login(email, password)
                )
            } catch (e: Exception) {
                IncidentUiState.Error(e.toString())
            }
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = IncidentUiState.Loading
            uiState = try {
                IncidentUiState.Success(
                    sarRepository.logout()
                )
            } catch (e: Exception) {
                IncidentUiState.Error(e.toString())
            }
        }
    }

    fun signUp(name: String, email: String, password: String, org: String, role: String) {
        this.name = name
        this.email = email
        this.password = password
        this.org = org
        this.role = role

        viewModelScope.launch(Dispatchers.IO) {
            uiState = IncidentUiState.Loading
            uiState = try {
                IncidentUiState.Success(
                    sarRepository.signUp(name, email, password, org, role)
                )
            } catch (e: Exception) {
                IncidentUiState.Error(e.toString())
            }
        }
    }
}
