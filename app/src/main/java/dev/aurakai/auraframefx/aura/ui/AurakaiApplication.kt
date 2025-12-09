package dev.aurakai.auraframefx.aura.ui

import android.app.Application

/**
 * Kept for backward compatibility. The real Hilt entrypoint is the root
 * `dev.aurakai.auraframefx.AurakaiApplication` class. This class remains
 * as a simple Application subclass to preserve references.
 */
open class AurakaiApplication : Application()
