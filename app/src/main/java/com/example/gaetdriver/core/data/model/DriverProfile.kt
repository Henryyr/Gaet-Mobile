package com.example.gaetdriver.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Data model matching the 'users' collection in Firestore.
 */
data class DriverProfile(
    @get:PropertyName("first_name") @set:PropertyName("first_name")
    var firstName: String = "",
    @get:PropertyName("last_name") @set:PropertyName("last_name")
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    @get:PropertyName("is_active") @set:PropertyName("is_active")
    var isActive: Boolean = true,
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp? = null,
    @get:PropertyName("update_at") @set:PropertyName("update_at")
    var updatedAt: Timestamp? = null,
    var bio: String = "",
    var location: String = "",
) {
    @get:Exclude
    val fullName: String get() = "$firstName $lastName".trim()
}
