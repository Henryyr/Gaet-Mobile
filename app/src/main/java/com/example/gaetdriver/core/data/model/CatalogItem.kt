package com.example.gaetdriver.core.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Data model for catalog items.
 */
data class CatalogItem(
    @get:Exclude @set:Exclude
    var id: String = "", 
    var userId: String = "",
    @get:PropertyName("imageBase64") @set:PropertyName("imageBase64")
    var imageBase64: String? = null,
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis(),
)
