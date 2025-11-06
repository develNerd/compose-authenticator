package org.com.composeauthenticator.platform.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import org.com.composeauthenticator.platform.ClipboardService

class AndroidClipboardService(private val context: Context) : ClipboardService {
    
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    
    override fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("TOTP Code", text)
        clipboardManager.setPrimaryClip(clip)
    }
    
    override fun showCopyFeedback(): String {
        return "Copied to clipboard!"
    }
}