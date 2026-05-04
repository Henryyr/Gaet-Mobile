package com.example.gaetdriver.core.firebase

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.network.AuthRepository
import com.example.gaetdriver.features.login.domain.LoginUseCase
import com.example.gaetdriver.features.login.domain.SignUpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A manager class to handle UI state for Authentication.
 * It uses UseCases for business logic and delegates auth state to [AuthRepository].
 */
class AuthManager(
    context: Context,
    private val scope: CoroutineScope
) {
    private val repository: AuthRepository = AuthRepository(context)
    private val loginUseCase: LoginUseCase = LoginUseCase(repository)
    private val signUpUseCase: SignUpUseCase = SignUpUseCase(repository)

    private val _isLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        repository.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email and password cannot be empty"
            return
        }

        scope.launch {
            loginUseCase(email, password).collect { resource ->
                handleResource(resource)
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank() || phone.isBlank()) {
            _error.value = "All fields are required"
            return
        }

        scope.launch {
            signUpUseCase(email, password, firstName, lastName, phone).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun handleResource(resource: Resource<Boolean>) {
        when (resource) {
            is Resource.Loading -> {
                _isLoading.value = true
                _error.value = null
            }
            is Resource.Success -> {
                _isLoading.value = false
            }
            is Resource.Error -> {
                _isLoading.value = false
                _error.value = resource.message
            }
            else -> {}
        }
    }

    fun signOut() {
        repository.signOut()
    }

    fun clearError() {
        _error.value = null
    }
}

/**
 * Composable function to initialize and remember AuthManager.
 */
@Composable
fun rememberAuthManager(): AuthManager {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(context, scope) { AuthManager(context, scope) }
}
