package org.com.composeauthenticator.platform.android

import org.com.composeauthenticator.platform.TimeService

class AndroidTimeService : TimeService {
    
    override fun getCurrentTime(): Long {
        return System.currentTimeMillis() / 1000L
    }
    
    override fun getTimeStep(): Long {
        return 30L // 30 seconds for TOTP
    }
}