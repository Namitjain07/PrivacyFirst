package com.secure.privacyfirst.data

enum class SecurityLevel {
    LOW,
    MEDIUM,
    HIGH;

    fun getDisplayName(): String {
        return when (this) {
            LOW -> "Low"
            MEDIUM -> "Medium"
            HIGH -> "High"
        }
    }

    fun getDescription(): String {
        return when (this) {
            LOW -> "HTTPS only + Downloads available"
            MEDIUM -> "HTTPS + SSL verification + Downloads available"
            HIGH -> "HTTPS + SSL + No screenshots/downloads + No copy/paste"
        }
    }

    fun getDetailedDescription(): String {
        return when (this) {
            LOW -> """
                • HTTPS connections only
                • File downloads enabled
                • Basic security level
            """.trimIndent()
            MEDIUM -> """
                • HTTPS connections only
                • SSL certificate verification
                • File downloads enabled
                • Enhanced security for banking
            """.trimIndent()
            HIGH -> """
                • HTTPS connections only
                • SSL certificate verification
                • Screenshot protection enabled
                • File downloads blocked
                • Copy/paste disabled
                • Maximum security level
            """.trimIndent()
        }
    }

    companion object {
        fun fromString(value: String): SecurityLevel {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                MEDIUM // Default to MEDIUM
            }
        }
    }
}
