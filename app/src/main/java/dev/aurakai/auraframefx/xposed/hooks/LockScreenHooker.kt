package dev.aurakai.auraframefx.xposed.hooks

import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenConfig

class LockScreenHooker(
    private val classLoader: ClassLoader,
    private val config: LockScreenConfig,
) {
    fun applyLockScreenHooks() {
        // TODO: Implement Xposed hooks for the Lock Screen
    }
}
