package com.example.gaetdriver.features.login.domain

import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.data.model.LoginRequest
import com.example.gaetdriver.core.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for handling the login business logic.
 */
class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(request: LoginRequest): Flow<Resource<Boolean>> {
        return repository.signIn(request.email, request.password)
    }
}
