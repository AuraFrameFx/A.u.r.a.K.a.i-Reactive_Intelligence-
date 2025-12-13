package dev.aurakai.auraframefx.system.lockscreen

import android.content.SharedPreferences
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenAnimation
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenConfig
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenElementType
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.ui.model.ImageResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockScreenCustomizer @Inject constructor(
    private val prefs: SharedPreferences,
) {

    private val _currentConfig = MutableStateFlow<LockScreenConfig?>(null)
    val currentConfig: StateFlow<LockScreenConfig?> = _currentConfig

    fun updateElementShape(elementType: LockScreenElementType, shape: OverlayShape) {
        // TODO: Implement
    }

    fun updateElementAnimation(elementType: LockScreenElementType, animation: LockScreenAnimation) {
        // TODO: Implement
    }

    fun updateBackground(image: ImageResource?) {
        // TODO: Implement
    }

    fun resetToDefault() {
        // TODO: Implement
    }
}
