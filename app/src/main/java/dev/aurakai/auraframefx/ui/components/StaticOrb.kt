package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.NeonPurple

/**
 * Orb state representing different visual appearances.
 */
enum class OrbState {
    IDLE,      // Passive state with subtle glow
    ACTIVE,    // Active processing with bright glow
    PULSING,   // Attention-seeking pulse animation
    ERROR,     // Error state with red tint
    SUCCESS    // Success state with green tint
}

/**
 * Static Orb UI component with multiple visual states.
 *
 * Displays a circular orb with different appearances based on state:
 * - IDLE: Subtle blue glow
 * - ACTIVE: Bright pulsing cyan glow
 * - PULSING: Strong pulse animation for attention
 * - ERROR: Red warning state
 * - SUCCESS: Green confirmation state
 *
 * @param modifier Compose modifier
 * @param state Visual state of the orb
 * @param text Optional text to display in center
 * @param size Size of the orb
 */
@Composable
fun StaticOrb(
    modifier: Modifier = Modifier,
    state: OrbState = OrbState.IDLE,
    text: String? = null,
    size: Dp = 80.dp,
    color: Color = NeonBlue, // For backward compatibility
) {
    // Animation for pulsing states
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowSize by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Determine colors based on state
    val (coreColor, glowColor, shouldPulse) = when (state) {
        OrbState.IDLE -> Triple(NeonBlue.copy(alpha = 0.6f), NeonBlue, false)
        OrbState.ACTIVE -> Triple(CyberpunkCyan.copy(alpha = 0.8f), CyberpunkCyan, true)
        OrbState.PULSING -> Triple(NeonPurple.copy(alpha = pulseAlpha), NeonPurple, true)
        OrbState.ERROR -> Triple(Color.Red.copy(alpha = 0.7f), Color.Red, true)
        OrbState.SUCCESS -> Triple(Color.Green.copy(alpha = 0.7f), Color.Green, false)
    }

    val effectiveAlpha = if (shouldPulse) pulseAlpha else 0.8f
    val effectiveGlowSize = if (shouldPulse) glowSize else 1f

    Box(
        modifier = modifier
            .size(size * effectiveGlowSize)
            .shadow(
                elevation = if (shouldPulse) (18.dp * effectiveGlowSize) else 12.dp,
                shape = CircleShape,
                ambientColor = glowColor,
                spotColor = glowColor
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow layer
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            coreColor.copy(alpha = effectiveAlpha),
                            glowColor.copy(alpha = effectiveAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Core orb
        Box(
            modifier = Modifier
                .size(size * 0.7f)
                .clip(CircleShape)
                .background(coreColor),
            contentAlignment = Alignment.Center
        ) {
            text?.let {
                Text(
                    text = it,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaticOrbPreview() { // Renamed
    StaticOrb(color = Color.Magenta, text = "Static")
}
