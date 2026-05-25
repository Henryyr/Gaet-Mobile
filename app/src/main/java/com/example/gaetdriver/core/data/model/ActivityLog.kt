package com.example.gaetdriver.core.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Data model for driver activity logs.
 */
data class ActivityLog(
    @get:Exclude @set:Exclude
    var id: String = "",
    var userId: String = "",
    var type: String = "", // e.g., "UPLOAD", "EDIT", "DELETE"
    var title: String = "",
    var description: String = "",
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)
