package dev.aurakai.auraframefx.utils

/**
 * Global logging utilities for the Genesis Protocol
 * Provides convenient shorthand for common logging operations
 * Delegates to AuraFxLogger companion object (Timber-backed)
 */

/**
 * Log an error message
 * @param tag The source tag/component
 * @param message The error message
 * @param throwable Optional exception
 */
fun e(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.error(tag, message, throwable)
}

/**
 * Log a warning message
 * @param tag The source tag/component
 * @param message The warning message
 * @param throwable Optional exception
 */
fun w(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.warn(tag, message, throwable)
}

/**
 * Log an info message
 * @param tag The source tag/component
 * @param message The info message
 * @param throwable Optional exception
 */
fun i(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.info(tag, message, throwable)
}

/**
 * Log a debug message
 * @param tag The source tag/component
 * @param message The debug message
 * @param throwable Optional exception
 */
fun d(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.debug(tag, message, throwable)
}

/**
 * Log a security event
 * @param tag The source tag/component
 * @param message The security message
 * @param throwable Optional exception
 */
fun s(tag: String, message: String, throwable: Throwable? = null) {
    AuraFxLogger.security(tag, message, throwable)
}

/**
 * Log performance metrics
 * @param tag The source tag/component
 * @param operation Name of the operation
 * @param durationMs Duration in milliseconds
 * @param metadata Optional metadata about the operation
 */
fun perf(
    tag: String,
    operation: String,
    durationMs: Long,
    metadata: Map<String, Any> = emptyMap()
) {
    AuraFxLogger.performance(tag, operation, durationMs, metadata)
}

/**
 * Log user interaction event
 * @param tag The source tag/component
 * @param action The user action
 * @param metadata Optional interaction metadata
 */
fun ua(tag: String, action: String, metadata: Map<String, Any> = emptyMap()) {
    AuraFxLogger.userInteraction(tag, action, metadata)
}

/**
 * Log AI operation
 * @param tag The source tag/component
 * @param operation Name of the operation
 * @param confidence Confidence score (0.0-1.0)
 * @param metadata Optional operation metadata
 */
fun ai(
    tag: String,
    operation: String,
    confidence: Float,
    metadata: Map<String, Any> = emptyMap()
) {
    AuraFxLogger.aiOperation(tag, operation, confidence, metadata)
}
