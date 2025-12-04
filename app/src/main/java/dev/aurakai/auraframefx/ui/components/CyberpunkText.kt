package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextStyle
import kotlin.random.Random

/**
 * Cyberpunk-themed text component with optional glitch effect.
 *
 * When glitch is enabled, applies a digital corruption effect with:
 * - RGB channel offset (chromatic aberration)
 * - Random horizontal offset jitter
 * - Periodic corruption flashes
 *
 * @param text The text to display
 * @param color Cyberpunk color theme
 * @param style Cyberpunk text typography
 * @param modifier Compose modifier
 * @param enableGlitch Whether to enable the glitch animation effect
 */
@Composable
fun CyberpunkText(
    text: String,
    color: CyberpunkTextColor,
    style: CyberpunkTextStyle,
    modifier: Modifier = Modifier,
    enableGlitch: Boolean = false,
) {
    if (enableGlitch) {
        GlitchText(
            text = text,
            color = color.color,
            style = style.textStyle,
            modifier = modifier
        )
    } else {
        Text(
            text = text,
            color = color.color,
            style = style.textStyle,
            modifier = modifier
        )
    }
}

/**
 * Text component with animated glitch effect.
 */
@Composable
private fun GlitchText(
    text: String,
    color: Color,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    // Glitch animation trigger
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")

    // Random glitch offset animation
    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_offset"
    )

    // RGB channel separation
    val rgbSeparation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rgb_separation"
    )

    // Random glitch trigger (occurs periodically)
    val glitchIntensity = remember {
        derivedStateOf {
            if (glitchOffset > 0.95f) Random.nextFloat() * 8f else 0f
        }
    }

    Box(modifier = modifier) {
        // Red channel (offset left)
        Text(
            text = text,
            color = Color.Red.copy(alpha = 0.5f),
            style = style,
            modifier = Modifier.offset(x = (-rgbSeparation - glitchIntensity.value).dp)
        )

        // Blue channel (offset right)
        Text(
            text = text,
            color = Color.Blue.copy(alpha = 0.5f),
            style = style,
            modifier = Modifier.offset(x = (rgbSeparation + glitchIntensity.value).dp)
        )

        // Main text (green/cyan channel)
        Text(
            text = text,
            color = color.copy(alpha = 0.9f),
            style = style,
            modifier = Modifier.offset(
                x = if (glitchIntensity.value > 5f) Random.nextInt(-2, 3).dp else 0.dp
            )
        )
    }
}
