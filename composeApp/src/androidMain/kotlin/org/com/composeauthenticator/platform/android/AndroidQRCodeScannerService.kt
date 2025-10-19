package org.com.composeauthenticator.platform.android

import org.com.composeauthenticator.platform.QRCodeScannerService
import org.com.composeauthenticator.platform.QRCodeResult

/**
 * Android QR Code Scanner Service using QRKit Compose Multiplatform
 * QRKit handles both Android and iOS implementation seamlessly
 */
class AndroidQRCodeScannerService : QRCodeScannerService {
    
    override suspend fun scanQRCode(): QRCodeResult {
        // QRKit integration will be handled in the Compose UI layer
        // This service becomes a simple interface since QRKit provides
        // the QrScanner composable that handles everything
        return QRCodeResult.Success("QRKit scanner will handle this in UI layer")
    }
}