package org.com.composeauthenticator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform