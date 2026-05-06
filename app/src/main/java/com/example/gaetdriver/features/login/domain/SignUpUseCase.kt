package com.example.gaetdriver.features.login.domain

import com.example.gaetdriver.core.base.Resource
import com.example.gaetdriver.core.data.model.RegisterRequest
import com.example.gaetdriver.core.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for handling the sign-up business logic.
 */
class SignUpUseCase(private val repository: AuthRepository) {
    operator fun invoke(request: RegisterRequest): Flow<Resource<Boolean>> {
        return repository.signUp(
            email = request.email,
            password = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
            phone = request.phone
        )
    }
}
