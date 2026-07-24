package com.example.gaetdriver.core.data.repository

import com.example.gaetdriver.core.data.model.ActivityLog
import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.data.model.DriverProfile
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.gaetdriver.core.base.AppException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PortfolioRepository(firestore: FirebaseFirestore) {

    private val usersCollection = firestore.collection("users")
    private val catalogCollection = firestore.collection("catalog")
    private val activitiesCollection = firestore.collection("activities")

    suspend fun logActivity(userId: String, type: String, title: String, description: String = "") {
        try {
            val log = ActivityLog(
                userId = userId,
                type = type,
                title = title,
                description = description,
            )
            activitiesCollection.add(log).await()
        } catch (_: Exception) {
            // Silently fail activity logging to avoid crashing critical flows
        }
    }

    /**
     * Gets all activity logs for a user.
     */
    fun getActivityLogs(userId: String): Flow<List<ActivityLog>> = callbackFlow {
        val subscription = activitiesCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ActivityLog::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                trySend(items.sortedByDescending { it.createdAt })
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Saves or updates the driver profile in 'users' collection.
     */
    suspend fun saveProfile(uid: String, profile: DriverProfile) {
        try {
            usersCollection.document(uid).set(profile).await()
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }

    /**
     * Gets the driver profile from 'users' collection.
     */
    suspend fun getProfile(uid: String): DriverProfile? {
        return try {
            usersCollection.document(uid).get().await().toObject(DriverProfile::class.java)
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }

    /**
     * Adds a new item to the catalog.
     */
    suspend fun addCatalogItem(item: CatalogItem) {
        try {
            if (item.id.isEmpty()) {
                catalogCollection.add(item).await()
            } else {
                catalogCollection.document(item.id).set(item).await()
            }
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }

    /**
     * Gets catalog items. Filtered by userId for privacy and limit check.
     */
    fun getCatalogItems(userId: String? = null): Flow<List<CatalogItem>> = callbackFlow {
        val query = if (userId != null) {
            catalogCollection.whereEqualTo("userId", userId)
        } else {
            catalogCollection
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(CatalogItem::class.java)?.apply { id = doc.id }
            } ?: emptyList()
            
            trySend(items.sortedByDescending { it.createdAt })
        }
        awaitClose { subscription.remove() }
    }

    /**
     * Precise count for the 5-item restriction.
     */
    suspend fun getUserItemCount(userId: String): Int {
        return try {
            val snapshot = catalogCollection.whereEqualTo("userId", userId).get().await()
            snapshot.size()
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }
    
    /**
     * Hard delete of the document.
     */
    suspend fun deleteCatalogItem(itemId: String) {
        try {
            if (itemId.isNotEmpty()) {
                catalogCollection.document(itemId).delete().await()
            }
        } catch (e: Exception) {
            throw AppException.from(e)
        }
    }
}

@Composable
fun rememberPortfolioRepository(): PortfolioRepository {
    val firestore = remember { FirebaseFirestore.getInstance() }
    return remember(firestore) { PortfolioRepository(firestore) }
}
