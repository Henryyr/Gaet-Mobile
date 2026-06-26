package com.example.gaetdriver.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale


object ImageUtils {

    private val memoryCache = object : LruCache<Int, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
        override fun sizeOf(key: Int, bitmap: Bitmap): Int {
            return bitmap.byteCount
        }
    }


    fun encodeToBase64(bitmap: Bitmap, quality: Int = 85, maxDimension: Int = 1600): String {
        val scaledBitmap = scaleBitmap(bitmap, maxDimension)
        val byteArrayOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    fun decodeFromBase64(base64String: String): Bitmap? {
        val key = base64String.hashCode()
        val cached = memoryCache.get(key)
        if (cached != null) return cached

        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val result = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            if (result != null) {
                memoryCache.put(key, result)
            }
            result
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "Decoding failed", e)
            null
        }
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
    
    fun clearCache() {
        memoryCache.evictAll()
    }
}
