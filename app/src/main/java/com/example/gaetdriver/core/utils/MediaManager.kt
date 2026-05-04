package com.example.gaetdriver.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

/**
 * A utility class to handle Camera and Gallery actions.
 */
class MediaManager(
    private val context: Context,
    private val pickMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val takePictureLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    fun launchCamera() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                takePictureLauncher.launch(null)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun launchGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

/**
 * Composable function to initialize and remember MediaManager.
 */
@Composable
fun rememberMediaManager(
    onImageCaptured: (Bitmap?) -> Unit,
    onImageSelected: (Uri?) -> Unit
): MediaManager {
    val context = androidx.compose.ui.platform.LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImageSelected(uri)
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        onImageCaptured(bitmap)
    }

    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePicture.launch(null)
        }
    }

    return remember(context, pickMedia, takePicture, requestPermission) {
        MediaManager(context, pickMedia, takePicture, requestPermission)
    }
}
