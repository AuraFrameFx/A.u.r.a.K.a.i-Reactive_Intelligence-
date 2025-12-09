package dev.aurakai.auraframefx.system.impl

/**
 * Minimal DefaultSystemMonitor stub used for DI bindings.
 */
interface DefaultSystemMonitor {
    fun isHealthy(): Boolean
}

class SimpleSystemMonitor : DefaultSystemMonitor {
    override fun isHealthy(): Boolean = true
}

