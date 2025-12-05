package com.zybooks.ai4sarapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zybooks.ai4sarapp.SarApplication
import com.zybooks.ai4sarapp.data.AuthResponse
import com.zybooks.ai4sarapp.data.FormDocument
import com.zybooks.ai4sarapp.data.IncidentData
import com.zybooks.ai4sarapp.data.IncidentDocument
import com.zybooks.ai4sarapp.data.SarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class IncidentUiState {
    data object Opened : IncidentUiState()
    data object Loading : IncidentUiState()
    data class Success(val response: Response<AuthResponse>) : IncidentUiState()
    data class Error(val error: String) : IncidentUiState()
}

class SarViewModel(
    private val sarRepository: SarRepository
) : ViewModel() {

    // Overall auth / API status
    var uiState: IncidentUiState by mutableStateOf(IncidentUiState.Opened)
        private set

    // Whether the user is considered logged in
    var isLoggedIn: Boolean by mutableStateOf(false)
        private set
    var hasIncidents: Boolean by mutableStateOf(false)
        private set
    var numIncidents: Int by mutableIntStateOf(0)
        private set

    // 👇 NEW: Forms state
    var selectedIncidentId: String? by mutableStateOf(null)
        private set

    var formsForIncident: List<FormDocument> by mutableStateOf(emptyList())
        private set

    var hasForms: Boolean by mutableStateOf(false)
        private set

    // Form fields
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
    var incidents: List<IncidentDocument> by mutableStateOf(emptyList())
        private set
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onOrgChange(newOrg: String) {
        org = newOrg
    }

    fun onRoleChange(newRole: String) {
        role = newRole
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
        viewModelScope.launch {
            uiState = IncidentUiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    sarRepository.login(email, password)
                }

                if (response.isSuccessful) {
                    isLoggedIn = true
                    uiState = IncidentUiState.Success(response)
                } else {
                    isLoggedIn = false
                    uiState = IncidentUiState.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                isLoggedIn = false
                uiState = IncidentUiState.Error(e.message ?: "Login failed.")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            uiState = IncidentUiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    sarRepository.logout()
                }

                // Even if the server logout fails, we’ll treat the user as logged-out locally
                isLoggedIn = false
                uiState = IncidentUiState.Success(response)
            } catch (e: Exception) {
                isLoggedIn = false
                uiState = IncidentUiState.Error(e.message ?: "Logout failed (local session cleared).")
            }
        }
    }

    fun signUp() {

        viewModelScope.launch {
            uiState = IncidentUiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    sarRepository.signUp(name, email, password, org, role)
                }

                if (response.isSuccessful) {
                    isLoggedIn = true
                    uiState = IncidentUiState.Success(response)
                } else {
                    isLoggedIn = false
                    uiState = IncidentUiState.Error("Sign up failed.")
                }
            } catch (e: Exception) {
                isLoggedIn = false
                uiState = IncidentUiState.Error(e.message ?: "Sign up failed.")
            }
        }
    }

    fun getIncidents() {
        viewModelScope.launch {
            uiState = IncidentUiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    sarRepository.getIncidents()
                }

                incidents = response
                hasIncidents = response.isNotEmpty()
                numIncidents = response.size

            } catch (e: Exception) {
                uiState = IncidentUiState.Error(e.message ?: "Couldn't fetch incidents")

            }
        }
    }

    fun loadFormsForIncident(incidentId: String) {
        selectedIncidentId = incidentId

        viewModelScope.launch {
            uiState = IncidentUiState.Loading
            try {
                val forms = withContext(Dispatchers.IO) {
                    sarRepository.getFormsForIncident(incidentId)
                }

                formsForIncident = forms
                hasForms = forms.isNotEmpty()
                // Optionally reset uiState to Opened or keep a separate state for forms
                uiState = IncidentUiState.Opened

            } catch (e: Exception) {
                formsForIncident = emptyList()
                hasForms = false
                uiState = IncidentUiState.Error(
                    e.message ?: "Couldn't fetch forms for incident."
                )
            }
        }
    }
}
