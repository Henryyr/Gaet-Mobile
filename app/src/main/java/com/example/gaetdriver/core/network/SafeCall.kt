package com.example.gaetdriver.core.network

import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

/**
 * A global utility to execute suspend calls safely with unified error handling.
 */
fun <T> safeCall(call: suspend () -> T): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    try {
        val result = call()
        emit(Resource.Success(result))
    } catch (e: Exception) {
        val appException = when (e) {
            is IOException -> AppException.NetworkException()
            is AppException -> e
            else -> AppException.UnknownException(e.localizedMessage ?: "Unknown Error")
        }
        emit(Resource.Error(appException.errorMessage))
    }
}
