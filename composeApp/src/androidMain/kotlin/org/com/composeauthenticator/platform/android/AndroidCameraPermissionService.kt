package org.com.composeauthenticator.platform.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.com.composeauthenticator.platform.CameraPermissionService

class AndroidCameraPermissionService(private val context: Context) : CameraPermissionService {
    
    override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun requestPermission(): Boolean {
        // For now, just return the current permission status
        // In a real app, you'd need to handle permission requests through the Activity
        // or use a permission library like Accompanist or similar
        return hasPermission()
    }
}