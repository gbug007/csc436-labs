package com.zybooks.ai4sarapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zybooks.ai4sarapp.data.FormDocument
import com.zybooks.ai4sarapp.data.IncidentDocument
import kotlinx.serialization.Serializable

// routes used by NavHost
sealed class Routes {
    @Serializable
    data object LoggedOut

    @Serializable
    data object Home

    @Serializable
    data object SignUp

    @Serializable
    data object Forms
}

// Entry point to the app
@Composable
fun SarApp() {
    val viewModel: SarViewModel = viewModel(factory = SarViewModel.Factory)
    val navController = rememberNavController()
    val isLoggedIn = viewModel.isLoggedIn

    // Handles logging in and out
    LaunchedEffect(isLoggedIn) {
        val target = if (isLoggedIn) Routes.Home else Routes.LoggedOut
        val targetRoute = target::class.qualifiedName
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // Avoid navigating to the same screen again
        if (currentRoute == targetRoute) return@LaunchedEffect

        navController.navigate(target) {
            // Clear the opposite screen from the back stack
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    // Routing is handled here for most of the different pages
    NavHost(
        navController = navController,
        startDestination = Routes.LoggedOut
    ) {
        // the screen you start on
        composable<Routes.LoggedOut> {
            LogInScreen(
                viewModel = viewModel,
                onLogIn = { viewModel.logIn() },
                onSignUp = { navController.navigate(Routes.SignUp)}
            )
        }
        // screen you get to on a successful login
        composable<Routes.Home> {
            viewModel.getIncidents()
            HomeScreen(
                viewModel = viewModel,
                onLogout = { viewModel.logOut() },
                onIncidentClick = {
                    viewModel.loadFormsForIncident(it.id)
                    navController.navigate(Routes.Forms)
                }
            )
        }
        // screen for making a new account
        composable<Routes.SignUp> {
            SignUpScreen(
                viewModel = viewModel,
                onSignUp = {viewModel.signUp()},
                onBackClick = {navController.navigate(Routes.LoggedOut)}
            )
        }
        // screen for viewing forms for each incident
        // note: no formatting is present for forms
        composable<Routes.Forms> {
            // go to specific form
            FormScreen(
                viewModel = viewModel,
                onBackClick = {navController.navigate(Routes.Home)}
            )
        }
    }
}

// Screen to login or sign up, starts when app starts
@Composable
fun LogInScreen(
    viewModel: SarViewModel,
    onLogIn: () -> Unit,
    onSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AI4SAR\nat Cal Poly",
            fontSize = 30.sp
        )
        Spacer(Modifier.height(16.dp))
        TextFieldWrapper(
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Enter Email"
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth()
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
                onClick = onSignUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Status text
        when (val state = viewModel.uiState) {
            is IncidentUiState.Opened -> {
                StatusLabel(
                    text = "Please log in or sign up.",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Loading -> {
                StatusLabel(
                    text = "Processing...",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Error -> {
                StatusLabel(
                    text = "Error: ${state.error}",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Success -> {
                if (!state.response.isSuccessful) {
                    StatusLabel(
                        text = "Invalid email or password.",
                        modifier = Modifier.align(Alignment.Start)
                    )
                } else {
                    // Optional: show nothing or "Login successful"
                }
            }
        }
    }
}

// Screen for entering information for signup
@Composable
fun SignUpScreen(
    viewModel: SarViewModel,
    onBackClick: () -> Unit,
    onSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextFieldWrapper(
            value = viewModel.name,
            onValueChange = viewModel::onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Enter Name"
        )
        Spacer(Modifier.height(12.dp))
        TextFieldWrapper(
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Enter Email"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        TextFieldWrapper(
            value = viewModel.org,
            onValueChange = viewModel::onOrgChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Enter Your Organization"
        )
        Spacer(Modifier.height(12.dp))
        TextFieldWrapper(
            value = viewModel.role,
            onValueChange = viewModel::onRoleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Enter Your Role"
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f)
            )
            SignUpButton(
                onClick = onSignUp,
                modifier = Modifier.weight(1f)
            )
        }
        // Status text
        when (val state = viewModel.uiState) {
            is IncidentUiState.Opened -> {
                StatusLabel(
                    text = "Please log in or sign up.",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Loading -> {
                StatusLabel(
                    text = "Processing...",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Error -> {
                StatusLabel(
                    text = "Error: ${state.error}",
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            is IncidentUiState.Success -> {
                if (!state.response.isSuccessful) {
                    StatusLabel(
                        text = "Invalid email or password.",
                        modifier = Modifier.align(Alignment.Start)
                    )
                } else {
                    // Optional: show nothing or "Login successful"
                }
            }
        }
    }


}

// Home screen displaying all the incidents
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SarViewModel,
    onLogout: () -> Unit,
    onIncidentClick: (IncidentDocument) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Incidents Dashboard")},
                actions = {
                    IconButton(onClick = {Unit}) {
                        // later: add logic for adding incidents
                        // note: not added because i don't have admin access
                        // to test this
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Incident"
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        bottomBar = {
            InfoBar(viewModel) {viewModel.getIncidents()}
        }
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(viewModel.incidents) {incident ->
                IncidentCard(
                    incident = incident,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onIncidentClick(incident) }
                )

            }
        }
    }
}

// Screen displaying list of forms
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: SarViewModel,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("${viewModel.selectedIncidentId} Incident Forms")},
                actions = {
                    IconButton(onClick = {Unit}) {
                        // later: use this to add a form
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Form"
                        )
                    }
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                }
            )
        },
        bottomBar = {
            InfoBar(viewModel) { viewModel.loadFormsForIncident(viewModel.selectedIncidentId?:"") }
        }
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(viewModel.formsForIncident) {form ->
                FormCard(
                    form = form,
                    modifier = Modifier
                        .fillMaxWidth()
                )

            }
        }
    }
}

// Screen displaying account information and form refresh
@Composable
fun InfoBar(viewModel: SarViewModel, refreshFunc: () -> Unit) {
    BottomAppBar(
        actions = {
            val showDialog = remember{mutableStateOf(false)}
            if (showDialog.value) {
                AccountInfoDialog(viewModel, { showDialog.value = false })
            }
            IconButton(onClick = {showDialog.value = true}) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Account info"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {refreshFunc}
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Data"
                )
            }
        }
    )
}

// Field for entering email
@Composable
fun TextFieldWrapper(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun IncidentCard(
    incident: IncidentDocument,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable {onClick()},
//            .padding(6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = incident.data.incidentName ?: "Untitled Incident",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = """
                    MP = Missing Person
                    MP Name: ${incident.data.missingPersonName ?: "Unknown"}
                    MP Age: ${incident.data.missingPersonAge ?: "Unknown"}
                    MP Sex: ${incident.data.missingPersonSex ?: "Unknown" }
                    
                    Incident Date: ${incident.data.incidentDate}
                    Reporting Person: ${incident.data.reportingPersonName}
                """.trimIndent()
            )

        }
    }
}

@Composable
fun FormCard(
    form: FormDocument,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Form id: ${form.id}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = """
                    Form data:
                    ${form.data}
                    """.trimIndent()
            )

        }
    }
}

// Field for entering password
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Enter Password") },
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
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Back")
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
fun AccountInfoDialog(viewModel: SarViewModel, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = """
                    Account information:
                    Email: ${viewModel.email}
                    Name: ${viewModel.name}
                    Org: ${viewModel.org}
                    Role: ${viewModel.role}
                    """.trimIndent(),
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight(),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun StatusLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
    )
}
