package com.example.gaetdriver.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Data model for catalog items.
 */
data class CatalogItem(
    @get:Exclude @set:Exclude
    var id: String = "", 
    var userId: String = "",
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    @get:PropertyName("imageBase64") @set:PropertyName("imageBase64")
    var imageBase64: String? = null,
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now(),
)
