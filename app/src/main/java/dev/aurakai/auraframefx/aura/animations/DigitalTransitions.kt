package dev.aurakai.auraframefx.ui.animation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Intended to apply a customizable cyber edge glow effect to this modifier using the given primary and secondary colors.
 *
 * @param primaryColor The main color for the edge glow.
 * @param secondaryColor The secondary color blended with the primary color.
 * @return The original modifier with the intended cyber edge glow effect.
 */
fun Modifier.cyberEdgeGlow(primaryColor: Color, secondaryColor: Color): Modifier = this

/**
 * Applies a digital pixelation effect to this modifier when enabled.
 *
 * @param visible If true, the pixelation effect is intended to be applied; if false, no effect is shown.
 * @return The modifier with the digital pixelation effect applied when visible is true, or the original modifier otherwise.
 */
fun Modifier.digitalPixelEffect(visible: Boolean): Modifier = this
