package com.example.todoapp.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.domain.intent.RegisterIntent
import com.example.todoapp.domain.state.RegisterState
import com.example.todoapp.presentation.ui.theme.customBlue
import com.example.todoapp.presentation.ui.theme.customTypography
import com.example.todoapp.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit
) {
    val registerState by viewModel.registerState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                onRegisterSuccess()
                navController.navigate("taskList") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is RegisterState.Error -> {
                // Hibakezelés (a UI része jeleníti meg az errorMessage-t)
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Text(
            text = "Register to TaskTask",
            style = customTypography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = customBlue,
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Register",
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
                onValueChange = { email = it },
                label = { Text("Email", style = customTypography.bodyMedium) },
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
                textStyle = customTypography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", style = customTypography.bodyMedium) },
                visualTransformation = PasswordVisualTransformation(),
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
                textStyle = customTypography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (registerState is RegisterState.Error) {
                Text(
                    text = (registerState as RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = customTypography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.handleIntent(RegisterIntent.Register(email, password))
                },
                enabled = registerState !is RegisterState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = customBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (registerState is RegisterState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Register", style = customTypography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = customBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Login", style = customTypography.bodyMedium)
            }
        }
    }
}
