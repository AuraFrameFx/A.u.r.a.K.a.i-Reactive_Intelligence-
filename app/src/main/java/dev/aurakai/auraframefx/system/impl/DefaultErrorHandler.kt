package dev.aurakai.auraframefx.system.impl

import dev.aurakai.auraframefx.ai.error.ErrorHandler
import dev.aurakai.auraframefx.ai.error.ErrorStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultErrorHandler @Inject constructor() : ErrorHandler {

    override fun handleError(error: Throwable, operation: String) {
        println("Error in $operation: ${error.message}")
    }

    override fun reportCriticalError(error: Throwable, context: String) {
        println("CRITICAL ERROR in $context: ${error.message}")
    }

    override fun getRecoverySuggestions(error: Throwable): List<String> {
        return listOf("Retry operation", "Check network connection")
    }

    override fun isRecoverable(error: Throwable): Boolean {
        return true
    }

    override fun getErrorStats(): ErrorStats {
        return ErrorStats(0, 0, 0, emptyMap())
    }
}
