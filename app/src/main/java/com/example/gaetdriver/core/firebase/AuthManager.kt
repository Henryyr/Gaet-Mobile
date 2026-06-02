package com.example.gaetdriver.core.firebase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.data.model.AuthState
import com.example.gaetdriver.core.data.model.LoginRequest
import com.example.gaetdriver.core.data.model.RegisterRequest
import com.example.gaetdriver.core.data.repository.AuthRepository
import com.example.gaetdriver.core.utils.DeviceManager
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
    private val scope: CoroutineScope,
    private val deviceManager: DeviceManager
) {
    private val repository: AuthRepository = AuthRepository()
    private val loginUseCase: LoginUseCase = LoginUseCase(repository)
    private val signUpUseCase: SignUpUseCase = SignUpUseCase(repository)

    private val _isLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isValidating = MutableStateFlow(false)
    val isValidating: StateFlow<Boolean> = _isValidating.asStateFlow()

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
                // Record login time for 1-day session policy
                scope.launch {
                    deviceManager.saveLoginTime(System.currentTimeMillis())
                }
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(isLoading = false, error = resource.message)
            }
            else -> {}
        }
    }

    fun signOut() {
        scope.launch {
            deviceManager.clearLoginTime()
            repository.signOut()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun validateSession() {
        val uid = currentUserId ?: return
        _isValidating.value = true
        scope.launch {
            try {
                // 1. Check if user still exists in Firestore (SERVER check)
                val profile = repository.getCurrentUserProfile(uid)
                if (profile == null) {
                    signOut()
                    return@launch
                }

                // 2. Check 1-day session expiry
                val lastLogin = deviceManager.getLoginTime()
                val currentTime = System.currentTimeMillis()
                val oneDayMillis = 24 * 60 * 60 * 1000L

                if (lastLogin == 0L || currentTime - lastLogin > oneDayMillis) {
                    signOut()
                }
            } catch (_: Exception) {
                // If there's an error (like database deleted/permission denied), kick them out for safety
                signOut()
            } finally {
                _isValidating.value = false
            }
        }
    }
}

/**
 * Composable function to initialize and remember AuthManager.
 */
@Composable
fun rememberAuthManager(): AuthManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceManager = remember { DeviceManager(context) }
    return remember(scope, deviceManager) { AuthManager(scope, deviceManager) }
}
