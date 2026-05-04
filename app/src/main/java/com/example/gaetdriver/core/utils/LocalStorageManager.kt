package com.example.gaetdriver.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * A utility class to manage local file storage for images.
 */
class LocalStorageManager(private val context: Context) {

    private val baseDir = "uploads"

    /**
     * Saves a Bitmap to the internal storage.
     * @param bitmap The bitmap to save.
     * @param fileName Optional filename, generates a random UUID if not provided.
     * @return The File object where the bitmap was saved, or null if failed.
     */
    fun saveBitmap(bitmap: Bitmap, fileName: String = "${UUID.randomUUID()}.jpg"): File? {
        val directory = File(context.filesDir, baseDir)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a Uri to the internal storage by copying its content.
     * @param uri The Uri to save.
     * @param fileName Optional filename, generates a random UUID if not provided.
     * @return The File object where the content was saved, or null if failed.
     */
    fun saveUri(uri: Uri, fileName: String = "${UUID.randomUUID()}.jpg"): File? {
        val directory = File(context.filesDir, baseDir)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a file from local storage.
     */
    fun deleteFile(file: File): Boolean {
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * Clears all files in the uploads directory.
     */
    fun clearAllUploads() {
        val directory = File(context.filesDir, baseDir)
        if (directory.exists()) {
            directory.listFiles()?.forEach { it.delete() }
        }
    }

    /**
     * Gets all files in the uploads directory.
     */
    fun getAllFiles(): List<File> {
        val directory = File(context.filesDir, baseDir)
        return directory.listFiles()?.toList() ?: emptyList()
    }

    /**
     * Gets a file from the uploads directory by name.
     */
    fun getFile(fileName: String): File {
        return File(File(context.filesDir, baseDir), fileName)
    }
}

/**
 * Helper to remember LocalStorageManager in Compose.
 */
@Composable
fun rememberLocalStorageManager(): LocalStorageManager {
    val context = LocalContext.current
    return remember(context) { LocalStorageManager(context) }
}
