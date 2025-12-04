package com.zybooks.ai4sarapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable


sealed class Routes {
    @Serializable
    data object LoggedOut

    @Serializable
    data object Home
}
@Composable
fun SarApp() {
    val viewModel: SarViewModel = viewModel(factory = SarViewModel.Factory)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LoggedOut
    ) {
        composable<Routes.LoggedOut> {
            LogInScreen(
                viewModel,
                onLogIn = {
                    viewModel.logIn()
                }
            )
        }
        composable<Routes.Home> {
            HomeScreen(
                viewModel,
                onLogout = {
                    println("Logout clicked: navigating to LoggedOut")
                    viewModel.logOut()
                    navController.navigate(route = Routes.LoggedOut) {
                        popUpTo(Routes.Home) {inclusive = true}
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


@Composable
fun LogInScreen(
    viewModel: SarViewModel,
    onLogIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Full-screen column, centered, with padding
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        UsernameField(
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogInButton(
                onLogIn = onLogIn,
                modifier = Modifier.weight(1f)
            )
            SignUpButton(
                onClick = { /* TODO: sign up flow */ },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "",
            modifier = Modifier.align(Alignment.Start)
        )
        val uiState = viewModel.uiState
        when (uiState) {
            is IncidentUiState.Error -> {
                StatusLabel("Login failed: $(state.error)")
            }
            is IncidentUiState.Success -> {
                if (!uiState.response.isSuccessful) {
                    StatusLabel("Invalid email or password.")
                }
            }
            is IncidentUiState.Loading -> {
                StatusLabel("Logging in...")
            }
            is IncidentUiState.Opened -> {
                StatusLabel("Please log in or sign up.")
            }
        }
    }
}

@Composable
fun UsernameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Enter email") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Enter password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier
    )
}

@Composable
fun LogInButton(
    onLogIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onLogIn, modifier = modifier) {
        Text("Login")
    }
}

@Composable
fun SignUpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Sign up")
    }
}

@Composable
fun HomeScreen(
    viewModel: SarViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome!")
        Spacer(Modifier.height(8.dp))
        Text("Logged in as: ${viewModel.email}")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Log out")
        }
    }
}

@Composable
fun StatusLabel(
    status: String
) {
    Text(
        text = status,
    )
}
