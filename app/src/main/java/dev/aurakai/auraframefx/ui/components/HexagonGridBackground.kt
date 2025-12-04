package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * Hexagon grid background with cyberpunk aesthetic.
 *
 * Renders a grid of hexagons with animated pulsing effect.
 * Hexagons are drawn with varying opacity based on their position
 * and animation state for a dynamic cyberpunk look.
 *
 * @param modifier Compose modifier
 * @param alpha Overall opacity of the grid (0f-1f)
 */
@Composable
fun HexagonGridBackground(modifier: Modifier = Modifier, alpha: Float = 0.3f) {
    // Animation for pulsing effect
    val infiniteTransition = rememberInfiniteTransition(label = "hexagon_pulse")

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val hexSize = 60f
        val hexWidth = hexSize * 2f
        val hexHeight = hexSize * 1.732f // √3 ≈ 1.732

        val cols = (size.width / (hexWidth * 0.75f)).toInt() + 2
        val rows = (size.height / hexHeight).toInt() + 2

        for (row in -1..rows) {
            for (col in -1..cols) {
                val offsetX = col * hexWidth * 0.75f
                val offsetY = row * hexHeight + (if (col % 2 == 0) hexHeight / 2f else 0f)

                // Calculate distance-based opacity
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val dx = offsetX - centerX
                val dy = offsetY - centerY
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                val maxDistance = kotlin.math.sqrt(centerX * centerX + centerY * centerY)
                val distanceFactor = 1f - (distance / maxDistance).coerceIn(0f, 1f)

                // Animated pulse based on position and time
                val pulse = sin(pulsePhase + (row * 0.3f) + (col * 0.2f)).toFloat()
                val hexAlpha = (alpha * 0.5f + (pulse * 0.3f * distanceFactor)).coerceIn(0f, 1f)

                // Alternate colors for visual interest
                val hexColor = if ((row + col) % 2 == 0) {
                    CyberpunkCyan.copy(alpha = hexAlpha)
                } else {
                    CyberpunkPink.copy(alpha = hexAlpha * 0.6f)
                }

                drawHexagon(
                    center = Offset(offsetX, offsetY),
                    size = hexSize,
                    color = hexColor
                )
            }
        }
    }
}

/**
 * Draws a single hexagon at the specified position.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagon(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path()

    for (i in 0..6) {
        val angle = (PI / 3.0 * i).toFloat()
        val x = center.x + size * cos(angle)
        val y = center.y + size * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2f)
    )
}
