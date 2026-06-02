package com.example.gaetdriver.core.data.repository

import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.Resource
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
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

        // Verify Firestore document exists
        val doc = db.collection("users").document(userId).get().await()
        if (!doc.exists()) {
            auth.signOut()
            throw AppException.AuthException("User profile not found in database")
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
        
        val userData = hashMapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "email" to email,
            "phone" to phone,
            "is_active" to true,
            "created_at" to Timestamp.now(),
            "update_at" to Timestamp.now()
        )
        
        db.collection("users").document(userId).set(userData).await()
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
        val doc = db.collection("users").document(uid).get().await()
        return if (doc.exists()) doc.data else null
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
        emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
    }
}
