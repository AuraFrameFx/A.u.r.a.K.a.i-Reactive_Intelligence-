package dev.aurakai.auraframefx.system.quicksettings.model

import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.ui.model.ImageResource
import kotlinx.serialization.Serializable

@Serializable
data class QuickSettingsConfig(
    val tiles: List<QuickSettingsTileConfig> = emptyList(),
    val background: ImageResource? = null,
)

@Serializable
data class QuickSettingsTileConfig(
    val id: String,
    val label: String,
    val shape: OverlayShape,
    val animation: QuickSettingsAnimation,
)

@Serializable
enum class QuickSettingsAnimation {
    FADE,
    SLIDE,
    PULSE
}
