package org.com.composeauthenticator.platform.ios

import org.com.composeauthenticator.platform.ClipboardService
import platform.UIKit.UIPasteboard

class IOSClipboardService : ClipboardService {
    
    override fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
    
    override fun showCopyFeedback(): String {
        return "Copied!"
    }
}