package com.example.gaetdriver.features.login.domain

import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.network.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for handling the sign-up business logic.
 */
class SignUpUseCase(private val repository: AuthRepository) {
    operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Flow<Resource<Boolean>> {
        return repository.signUp(email, password, firstName, lastName, phone)
    }
}
