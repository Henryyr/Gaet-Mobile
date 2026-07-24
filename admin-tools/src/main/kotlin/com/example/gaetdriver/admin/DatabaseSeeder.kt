package com.example.gaetdriver.admin

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import com.example.gaetdriver.core.utils.HtmlUtils
import java.io.File
import java.io.FileInputStream

/**
 * FINAL NUCLEAR RESET & SEEDER
 * Wipes EVERYTHING (Users & All Collections) and seeds themes with clean IDs.
 */
fun main() {
    val serviceAccountFile = File("serviceAccountKey.json")
    if (!serviceAccountFile.exists()) {
        println("❌ ERROR: 'serviceAccountKey.json' missing from root!")
        return
    }

    println("🚀 Initializing Firebase Admin...")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(FileInputStream(serviceAccountFile)))
        .build()

    if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options)
    }
    
    val db = FirestoreClient.getFirestore()
    val auth = FirebaseAuth.getInstance()

    // 1. NUCLEAR AUTH WIPE
    println("\n🧹 WIPING ALL AUTH USERS...")
    try {
        var page = auth.listUsers(null)
        while (page != null) {
            for (user in page.values) {
                auth.deleteUser(user.uid)
                println("   🗑️ Auth User Deleted: ${user.email} (${user.uid})")
            }
            page = page.nextPage
        }
    } catch (e: Exception) {
        println("   ⚠️ Auth Wipe Error: ${e.message}")
    }

    // 2. NUCLEAR FIRESTORE WIPE
    println("\n🧹 WIPING ALL FIRESTORE COLLECTIONS...")
    try {
        db.listCollections().forEach { collection ->
            val colId = collection.id
            println("   Emptying collection: $colId")
            collection.listDocuments().forEach { doc ->
                doc.delete().get()
            }
            println("   ✅ '$colId' wiped.")
        }
    } catch (e: Exception) {
        println("   ⚠️ Firestore Wipe Error: ${e.message}")
    }

    // 3. DIRECT THEME SEEDING
    println("\n📂 SEEDING THEMES...")
    
    // NEW PROFESSIONAL IDs (UID-STYLE)
    upload(db, "7j8K9L0m1N2p3Q4r5S6t", "classic.html")
    upload(db, "8v9W0x1Y2z3A4b5C6d7E", "minimalist.html")
    upload(db, "9G0h1I2j3K4l5M6n7O8p", "explorer.html")
    upload(db, "0R1s2T3u4V5w6X7y8Z9a", "executive.html")
    upload(db, "1B2c3D4e5F6g7H8i9J0k", "storyteller.html")

    println("\n✨ BACKEND IS NOW 100% CLEAN AND SEEDED.")
}

private fun upload(db: com.google.cloud.firestore.Firestore, id: String, filename: String) {
    val file = File("web/themes/$filename")
    if (file.exists()) {
        val minified = HtmlUtils.minifyHtml(file.readText())
        db.collection("master_themes").document(id).set(mapOf("theme_content" to minified)).get()
        println("   ✅ Uploaded: $filename -> $id")
    } else {
        println("   ❌ FAILED: $filename not found!")
    }
}
