package dev.aurakai.auraframefx.model

interface ContextAwareAgent {
    fun setContext(context: Map<String, Any>)
}
