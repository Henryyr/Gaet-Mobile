package com.example.gaetdriver.core.base

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Global Exception class to handle various application errors.
 */
sealed class AppException(val errorMessage: String) : Exception(errorMessage) {
    class NetworkException(message: String = "No internet connection") : AppException(message)
    class AuthException(message: String) : AppException(message)
    class ServerException(message: String = "Internal server error") : AppException(message)
    class UnknownException(message: String = "An unexpected error occurred") : AppException(message)

    companion object {
        /**
         * Maps a generic Exception (or Firebase Exception) to a specific AppException.
         */
        fun from(e: Throwable): AppException {
            return when (e) {
                is AppException -> e
                is FirebaseNetworkException -> NetworkException("Network error. Please check your connection.")
                is FirebaseAuthException -> AuthException(e.localizedMessage ?: "Authentication failed")
                is FirebaseFirestoreException -> ServerException("Database error: ${e.code.name}")
                else -> {
                    val message = e.localizedMessage
                    if (message?.contains("network", ignoreCase = true) == true || 
                        message?.contains("connection", ignoreCase = true) == true) {
                        NetworkException()
                    } else {
                        UnknownException(message ?: "An unexpected error occurred")
                    }
                }
            }
        }
    }
}
