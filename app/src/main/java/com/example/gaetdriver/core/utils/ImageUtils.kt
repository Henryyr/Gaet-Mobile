package com.example.gaetdriver.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

/**
 * Utility to handle image encoding for Firestore (saving Storage costs).
 * Note: Firestore document limit is 1MB. Images are compressed and resized.
 */
object ImageUtils {

    /**
     * Encodes a Bitmap to a Base64 string.
     * Resizes the bitmap to a maximum dimension to ensure it fits in Firestore.
     */
    fun encodeToBase64(bitmap: Bitmap, quality: Int = 70, maxDimension: Int = 400): String {
        val scaledBitmap = scaleBitmap(bitmap, maxDimension)
        val byteArrayOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Decodes a Base64 string back to a Bitmap.
     */
    fun decodeFromBase64(base64String: String): Bitmap? {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val result = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            return result
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / aspectRatio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * aspectRatio).toInt()
        }

        return bitmap.scale(newWidth, newHeight)
    }
}
