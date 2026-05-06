package com.example.gaetdriver.core.data.model

/**
 * Request model for Login.
 */
data class LoginRequest(
    val email: String = "",
    val password: String = ""
)

/**
 * Request model for Registration.
 */
data class RegisterRequest(
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = ""
)

/**
 * UI State for Auth screens.
 */
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
