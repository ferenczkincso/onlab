package com.example.todoapp.presentation.ui.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.todoapp.domain.intent.LoginIntent
import com.example.todoapp.domain.state.LoginState
import com.example.todoapp.presentation.ui.theme.customBlue
import com.example.todoapp.presentation.ui.theme.customTypography
import com.example.todoapp.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Set up the Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleIntent(LoginIntent.GoogleSignIn(task))
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.LoggedIn) {
            Log.d("LoginScreen", "Navigating to TaskListScreen")
            onLoginSuccess()
            navController.navigate("taskList") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Text(
            text = "Welcome to TaskTask",
            style = customTypography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = customBlue,
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Login",
            style = customTypography.titleLarge,
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                textStyle = customTypography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFD6D8DE),
                    focusedContainerColor = Color(0xFFD6D8DE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.Gray,
                    disabledLabelColor = Color.LightGray,
                    cursorColor = Color.DarkGray
                ),
                onValueChange = { email = it },
                label = { Text("Email", style = customTypography.bodyMedium) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                textStyle = customTypography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFD6D8DE),
                    focusedContainerColor = Color(0xFFD6D8DE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.Gray,
                    disabledLabelColor = Color.LightGray,
                    cursorColor = Color.DarkGray
                ),
                onValueChange = { password = it },
                label = { Text("Password", style = customTypography.bodyMedium) },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (loginState is LoginState.LoggingError) {
                val errorMessage = (loginState as LoginState.LoggingError).message
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.handleIntent(LoginIntent.Login(email, password))
                },
                colors = ButtonDefaults.buttonColors(containerColor = customBlue),
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Log in", style = customTypography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = customTypography.bodyMedium,
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Google Sign-In button
            Button(
                onClick = {
                    launcher.launch(viewModel.getGoogleSignInClient().signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = loginState !is LoginState.Loading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Sign in with Google",
                        style = customTypography.bodyMedium
                    )
                    
                    if (loginState is LoginState.Loading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = customBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("register") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = customBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account", style = customTypography.bodyMedium)
            }
        }
    }
}