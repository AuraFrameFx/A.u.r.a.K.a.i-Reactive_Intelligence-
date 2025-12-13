package dev.aurakai.auraframefx.utils

/**
 * Global logging extensions that provide shorthand methods for common logging operations.
 * These are backed by AuraFxLogger and can be used throughout the application without
 * needing to reference the logger explicitly.
 */

/**
 * Logs a debug-level message.
 * Shorthand for: AuraFxLogger.debug(tag, message, throwable)
 */
fun d(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.debug(tag, message, throwable)
}

/**
 * Logs an informational message.
 * Shorthand for: AuraFxLogger.info(tag, message, throwable)
 */
fun i(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.info(tag, message, throwable)
}

/**
 * Logs a warning message.
 * Shorthand for: AuraFxLogger.warn(tag, message, throwable)
 */
fun w(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.warn(tag, message, throwable)
}

/**
 * Logs an error message.
 * Shorthand for: AuraFxLogger.error(tag, message, throwable)
 */
fun e(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.error(tag, message, throwable)
}

/**
 * Logs a security event.
 */
fun security(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.security(tag, message, throwable)
}
