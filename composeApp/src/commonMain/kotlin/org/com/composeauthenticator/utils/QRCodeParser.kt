package org.com.composeauthenticator.utils

data class OTPAuthUri(
    val secret: String,
    val issuer: String?,
    val accountName: String?,
    val algorithm: String = "SHA1",
    val digits: Int = 6,
    val period: Int = 30
)

object QRCodeParser {
    
    fun parseOTPAuthUri(uri: String): OTPAuthUri? {
        try {
            val parsedUri = parseUri(uri)
            
            if (parsedUri.scheme != "otpauth" || parsedUri.host != "totp") {
                return null
            }
            
            val path = parsedUri.path?.removePrefix("/")
            val queryParams = parseQueryParameters(parsedUri.query ?: "")
            
            val secret = queryParams["secret"] ?: return null
            val algorithm = queryParams["algorithm"] ?: "SHA1"
            val digits = queryParams["digits"]?.toIntOrNull() ?: 6
            val period = queryParams["period"]?.toIntOrNull() ?: 30
            
            // Parse the path which can be in format "issuer:account" or just "account"
            val (pathIssuer, accountName) = if (path?.contains(":") == true) {
                val parts = path.split(":", limit = 2)
                urlDecode(parts[0]) to urlDecode(parts[1])
            } else {
                null to path?.let { urlDecode(it) }
            }
            
            // Prefer the issuer from query params, fallback to path issuer
            val finalIssuer = queryParams["issuer"] ?: pathIssuer
            
            return OTPAuthUri(
                secret = secret,
                issuer = finalIssuer,
                accountName = accountName,
                algorithm = algorithm,
                digits = digits,
                period = period
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    private data class ParsedUri(
        val scheme: String?,
        val host: String?,
        val path: String?,
        val query: String?
    )
    
    private fun parseUri(uri: String): ParsedUri {
        val schemeEndIndex = uri.indexOf("://")
        if (schemeEndIndex == -1) return ParsedUri(null, null, null, null)
        
        val scheme = uri.substring(0, schemeEndIndex)
        val remaining = uri.substring(schemeEndIndex + 3)
        
        val queryStartIndex = remaining.indexOf('?')
        val pathAndHost = if (queryStartIndex != -1) remaining.substring(0, queryStartIndex) else remaining
        val query = if (queryStartIndex != -1) remaining.substring(queryStartIndex + 1) else null
        
        val pathStartIndex = pathAndHost.indexOf('/')
        val host = if (pathStartIndex != -1) pathAndHost.substring(0, pathStartIndex) else pathAndHost
        val path = if (pathStartIndex != -1) pathAndHost.substring(pathStartIndex) else null
        
        return ParsedUri(scheme, host, path, query)
    }
    
    private fun urlDecode(encoded: String): String {
        var result = encoded
        
        // Common URL encoded characters
        val decodingMap = mapOf(
            "%20" to " ",
            "%21" to "!",
            "%22" to "\"",
            "%23" to "#",
            "%24" to "$",
            "%25" to "%",
            "%26" to "&",
            "%27" to "'",
            "%28" to "(",
            "%29" to ")",
            "%2A" to "*",
            "%2B" to "+",
            "%2C" to ",",
            "%2D" to "-",
            "%2E" to ".",
            "%2F" to "/",
            "%3A" to ":",
            "%3B" to ";",
            "%3C" to "<",
            "%3D" to "=",
            "%3E" to ">",
            "%3F" to "?",
            "%40" to "@",
            "%5B" to "[",
            "%5C" to "\\",
            "%5D" to "]",
            "%5E" to "^",
            "%5F" to "_",
            "%60" to "`",
            "%7B" to "{",
            "%7C" to "|",
            "%7D" to "}",
            "%7E" to "~"
        )
        
        for ((encoded, decoded) in decodingMap) {
            result = result.replace(encoded, decoded, ignoreCase = true)
        }
        
        // Handle generic hex encoding %XX
        val hexPattern = Regex("%([0-9A-Fa-f]{2})")
        result = hexPattern.replace(result) { matchResult ->
            val hexValue = matchResult.groupValues[1]
            val byteValue = hexValue.toInt(16).toByte()
            byteValue.toInt().toChar().toString()
        }
        
        return result
    }
    
    private fun parseQueryParameters(query: String): Map<String, String> {
        return query.split("&")
            .mapNotNull { param ->
                val parts = param.split("=", limit = 2)
                if (parts.size == 2) {
                    urlDecode(parts[0]) to urlDecode(parts[1])
                } else null
            }
            .toMap()
    }
    
    fun isValidOTPAuthUri(uri: String): Boolean {
        return parseOTPAuthUri(uri) != null
    }
}