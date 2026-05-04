package com.example.gaetdriver.features.login.domain

import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.network.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for handling the login business logic.
 */
class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Resource<Boolean>> {
        return repository.signIn(email, password)
    }
}
