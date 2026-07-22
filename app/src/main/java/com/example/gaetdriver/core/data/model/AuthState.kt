package com.example.gaetdriver.core.data.model

/**
 * UI State for Auth screens.
 */
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
