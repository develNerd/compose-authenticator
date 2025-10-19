package org.com.composeauthenticator.platform.ios

import org.com.composeauthenticator.platform.CameraPermissionService
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class IOSCameraPermissionService : CameraPermissionService {
    
    override suspend fun hasPermission(): Boolean {
        return AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
    }
    
    override suspend fun requestPermission(): Boolean {
        if (hasPermission()) return true
        
        return suspendCancellableCoroutine { continuation ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                continuation.resume(granted)
            }
        }
    }
}