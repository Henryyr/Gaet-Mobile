package com.example.gaetdriver.features.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.AppTextField

@Composable
fun LoginBody(
    authManager: AuthManager,
    isLoginMode: Boolean,
    onModeToggle: () -> Unit
) {
    val strings = LocalStrings.current
    
    // These states will automatically reset when LoginScreen changes the 'key'
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    val isLoading by authManager.isLoading.collectAsState()
    val error by authManager.error.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isLoginMode) {
            AppTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = strings.firstName,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            AppTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = strings.lastName,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = strings.phoneNumber,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
        }

        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = strings.email,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = strings.password,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            error = error // Display error message near the password field or use a global error component
        )

        AppButton(
            text = if (isLoginMode) strings.login else strings.signUp,
            onClick = {
                if (isLoginMode) {
                    authManager.signIn(email, password)
                } else {
                    authManager.signUp(email, password, firstName, lastName, phone)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading
        )

        TextButton(
            onClick = onModeToggle,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            val toggleText = if (isLoginMode) strings.dontHaveAccount else strings.alreadyHaveAccount
            Text(
                text = toggleText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
