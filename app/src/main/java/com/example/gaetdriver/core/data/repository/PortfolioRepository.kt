package com.example.gaetdriver.core.data.repository

import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.data.model.DriverProfile
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PortfolioRepository(firestore: FirebaseFirestore) {

    private val usersCollection = firestore.collection("users")
    private val catalogCollection = firestore.collection("catalog")

    /**
     * Saves or updates the driver profile in 'users' collection.
     */
    suspend fun saveProfile(uid: String, profile: DriverProfile) {
        usersCollection.document(uid).set(profile).await()
    }

    /**
     * Gets the driver profile from 'users' collection.
     */
    suspend fun getProfile(uid: String): DriverProfile? {
        return usersCollection.document(uid).get().await().toObject(DriverProfile::class.java)
    }

    /**
     * Adds a new item to the catalog.
     */
    suspend fun addCatalogItem(item: CatalogItem) {
        catalogCollection.add(item).await()
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
        } catch (_: Exception) {
            0
        }
    }
    
    /**
     * Hard delete of the document.
     */
    suspend fun deleteCatalogItem(itemId: String) {
        if (itemId.isNotEmpty()) {
            catalogCollection.document(itemId).delete().await()
        }
    }
}

@Composable
fun rememberPortfolioRepository(): PortfolioRepository {
    val firestore = remember { FirebaseFirestore.getInstance() }
    return remember(firestore) { PortfolioRepository(firestore) }
}
