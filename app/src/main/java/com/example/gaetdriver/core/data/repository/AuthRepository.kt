package com.example.gaetdriver.core.data.repository

import android.util.Log
import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.Resource
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Repository to handle authentication and user profile data in Firestore.
 */
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val db: FirebaseFirestore = Firebase.firestore,
) {
    fun signIn(email: String, password: String): Flow<Resource<Boolean>> = safeCall {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw AppException.AuthException("Login failed")

        // Force SERVER fetch to bypass local cache
        val doc = db.collection("users").document(userId).get(Source.SERVER).await()
        if (!doc.exists()) {
            auth.signOut()
            throw AppException.AuthException("User profile not found. The account might have been deleted from the database.")
        }
        true
    }

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Flow<Resource<Boolean>> = safeCall {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid ?: throw AppException.AuthException("Registration failed")
        
        try {
            val userData = hashMapOf(
                "first_name" to firstName,
                "last_name" to lastName,
                "email" to email,
                "phone" to phone,
                "is_active" to true,
                "onboarding_completed" to false,
                "created_at" to Timestamp.now(),
                "update_at" to Timestamp.now()
            )
            
            db.collection("users").document(userId).set(userData).await()
        } catch (e: Exception) {
            // If Firestore fails, we should ideally delete the Auth user to allow retry
            result.user?.delete()?.await()
            throw AppException.from(e)
        }
        true
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun addAuthStateListener(listener: (FirebaseAuth) -> Unit) {
        auth.addAuthStateListener(listener)
    }

    suspend fun getCurrentUserProfile(uid: String): Any? {
        try {
            // Force SERVER fetch to bypass local cache
            val doc = db.collection("users").document(uid).get(Source.SERVER).await()
            return if (doc.exists()) doc.data else null
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }
}

/**
 * Helper to handle Firebase exceptions in a Flow.
 */
private fun <T> safeCall(action: suspend () -> T): Flow<Resource<T>> = kotlinx.coroutines.flow.flow {
    emit(Resource.Loading)
    try {
        emit(Resource.Success(action()))
    } catch (e: Exception) {
        Log.e("AuthRepository", "Operation failed", e)
        val appException = AppException.from(e)
        emit(Resource.Error(appException.errorMessage))
    }
}
