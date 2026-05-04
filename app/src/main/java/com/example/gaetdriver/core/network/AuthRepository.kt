package com.example.gaetdriver.core.network

import android.content.Context
import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.Resource
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Repository to handle authentication and user profile data in Firestore.
 */
class AuthRepository(
    context: Context,
    private val auth: FirebaseAuth = Firebase.auth,
    private val db: FirebaseFirestore = Firebase.firestore,
) {
    private val sessionManager = SessionManager(context)
    private val externalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun signIn(email: String, password: String): Flow<Resource<Boolean>> = safeCall {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid ?: throw AppException.AuthException("User not found")
        
        externalScope.launch {
            sessionManager.saveSession(userId, email)
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
        
        externalScope.launch {
            sessionManager.saveSession(userId, email)
        }
        true
    }

    fun signOut() {
        auth.signOut()
        externalScope.launch {
            sessionManager.clearSession()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun addAuthStateListener(listener: (FirebaseAuth) -> Unit) {
        auth.addAuthStateListener(listener)
    }

    val userSession: Flow<UserSession?> = sessionManager.userSession
}
