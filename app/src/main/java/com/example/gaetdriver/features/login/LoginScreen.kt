package com.example.gaetdriver.features.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.layout.ViewLayout
import com.example.gaetdriver.features.login.ui.LoginBody
import com.example.gaetdriver.features.login.ui.LoginHeader

@Composable
fun LoginScreen(
    authManager: AuthManager,
) {
    val focusManager = LocalFocusManager.current
    var isLoginMode by remember { mutableStateOf(true) }

    ViewLayout(
        header = {
            LoginHeader(isLoginMode = isLoginMode)
        },
        body = {
            key(isLoginMode) {
                LoginBody(
                    authManager = authManager,
                    isLoginMode = isLoginMode,
                    onModeToggle = { 
                        focusManager.clearFocus()
                        authManager.clearError()
                        isLoginMode = !isLoginMode 
                    }
                )
            }
        }
    )
}
