package com.example.gaetdriver.core.firebase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.data.model.AuthState
import com.example.gaetdriver.core.data.model.LoginRequest
import com.example.gaetdriver.core.data.model.RegisterRequest
import com.example.gaetdriver.core.data.repository.AuthRepository
import com.example.gaetdriver.features.login.domain.LoginUseCase
import com.example.gaetdriver.features.login.domain.SignUpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A manager class to handle UI state for Authentication.
 */
class AuthManager(
    private val scope: CoroutineScope
) {
    private val repository: AuthRepository = AuthRepository()
    private val loginUseCase: LoginUseCase = LoginUseCase(repository)
    private val signUpUseCase: SignUpUseCase = SignUpUseCase(repository)

    private val _isLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val currentUserId: String? get() = repository.getCurrentUserId()

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        repository.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
        }
    }

    fun signIn(request: LoginRequest) {
        if (request.email.isBlank() || request.password.isBlank()) {
            _state.value = _state.value.copy(error = "Email and password cannot be empty")
            return
        }

        scope.launch {
            loginUseCase(request).collect { resource ->
                handleResource(resource)
            }
        }
    }

    fun signUp(request: RegisterRequest) {
        if (request.email.isBlank() || request.password.isBlank() || request.firstName.isBlank() || request.lastName.isBlank() || request.phone.isBlank()) {
            _state.value = _state.value.copy(error = "All fields are required")
            return
        }

        scope.launch {
            signUpUseCase(request).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun handleResource(resource: Resource<Boolean>) {
        when (resource) {
            is Resource.Loading -> {
                _state.value = _state.value.copy(isLoading = true, error = null)
            }
            is Resource.Success -> {
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(isLoading = false, error = resource.message)
            }
            else -> {}
        }
    }

    fun signOut() {
        repository.signOut()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

/**
 * Composable function to initialize and remember AuthManager.
 */
@Composable
fun rememberAuthManager(): AuthManager {
    val scope = rememberCoroutineScope()
    return remember(scope) { AuthManager(scope) }
}
