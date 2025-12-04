package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal

/**
 * Aura Sparkle Button with animated glow and shimmer effects.
 *
 * Features:
 * - Pulsing glow effect using scale animation
 * - Shimmer gradient background
 * - Neon-styled shadow
 * - Smooth interaction animations
 *
 * @param onClick Callback invoked when button is clicked
 * @param modifier Optional modifier for the button
 * @param androidx.compose.ui.semantics.text Button text to display
 */
@Composable
fun AuraSparkleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { Text("Sparkle") },
) {
    // Infinite pulsing animation for glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle_glow")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Box(modifier = modifier) {
        // Glow layer (behind button)
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(glowScale)
                .alpha(0.5f)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonTeal.copy(alpha = shimmerAlpha),
                            NeonPurple.copy(alpha = shimmerAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // Actual button
        Button(
            onClick = onClick,
            modifier = Modifier
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = NeonTeal,
                    ambientColor = NeonPurple
                ),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple.copy(alpha = 0.8f),
                contentColor = NeonTeal
            )
        ) {
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuraSparkleButtonPreview() { // Renamed
    AuraSparkleButton(onClick = {})
}
