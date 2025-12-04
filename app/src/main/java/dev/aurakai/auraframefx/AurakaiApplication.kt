package dev.aurakai.auraframefx

import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.aura.ui.AurakaiApplication as AuraApp

/**
 * Root-level application wrapper.
 * Some packaging or tooling paths expect the application class at the root
 * package. This class simply delegates to the real application implementation
 * located at `dev.aurakai.auraframefx.aura.ui.AurakaiApplication`.
 */
@HiltAndroidApp
class AurakaiApplication : AuraApp()
