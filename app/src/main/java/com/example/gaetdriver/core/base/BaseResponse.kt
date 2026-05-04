package com.example.gaetdriver.core.base

/**
 * Standardized wrapper for all API or Database responses.
 */
data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null
)
