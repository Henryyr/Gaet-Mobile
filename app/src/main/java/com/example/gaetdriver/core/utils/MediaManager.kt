package com.example.gaetdriver.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

/**
 * A utility class to handle Camera and Gallery actions with High Resolution support.
 */
class MediaManager(
    private val context: Context,
    private val pickMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val takePictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val tempUri: Uri?
) {
    fun launchCamera() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                tempUri?.let { takePictureLauncher.launch(it) }
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

@Composable
fun rememberMediaManager(
    onImageCaptured: (Uri?) -> Unit,
    onImageSelected: (Uri?) -> Unit
): MediaManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Create a temp file for camera
    val tempUri = remember {
        val directory = File(context.cacheDir, "images")
        if (!directory.exists()) directory.mkdirs()
        val file = File(directory, "camera_capture.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImageSelected(uri)
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            onImageCaptured(tempUri)
        }
    }

    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePicture.launch(tempUri)
        }
    }

    return remember(context, pickMedia, takePicture, requestPermission, tempUri) {
        MediaManager(context, pickMedia, takePicture, requestPermission, tempUri)
    }
}
