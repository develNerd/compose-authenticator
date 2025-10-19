package org.com.composeauthenticator.platform.ios

import org.com.composeauthenticator.platform.TimeService
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class IOSTimeService : TimeService {
    
    override fun getCurrentTime(): Long {
        return NSDate().timeIntervalSince1970().toLong()
    }
    
    override fun getTimeStep(): Long {
        return 30L // 30 seconds for TOTP
    }
}