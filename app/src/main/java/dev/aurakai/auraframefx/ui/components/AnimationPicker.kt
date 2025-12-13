package dev.aurakai.auraframefx.ui.components

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenAnimation
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsAnimation

@Composable
fun AnimationPicker(
    currentAnimation: QuickSettingsAnimation,
    onAnimationSelected: (QuickSettingsAnimation) -> Unit,
) {
    // TODO: Implement animation picker UI
}

@Composable
fun AnimationPicker(
    currentAnimation: LockScreenAnimation,
    onAnimationSelected: (LockScreenAnimation) -> Unit,
) {
    // TODO: Implement animation picker UI
}
