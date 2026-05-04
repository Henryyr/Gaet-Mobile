package com.example.gaetdriver.core.base

/**
 * Global Exception class to handle various application errors.
 */
sealed class AppException(val errorMessage: String) : Exception(errorMessage) {
    class NetworkException(message: String = "No internet connection") : AppException(message)
    class AuthException(message: String) : AppException(message)
    class ServerException(message: String = "Internal server error") : AppException(message)
    class UnknownException(message: String = "An unexpected error occurred") : AppException(message)
}
