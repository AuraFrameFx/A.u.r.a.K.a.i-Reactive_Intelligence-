package dev.aurakai.auraframefx.ai.agents

/**
 * Minimal placeholder for AuraAgent used by DI/bindings.
 */
interface AuraAgent {
    fun id(): String
}

class MockAuraAgent : AuraAgent {
    override fun id(): String = "aura-agent-stub"
}

