package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import kotlin.math.pow

/**
 * Digital landscape background with perspective grid (Tron-typography).
 *
 * Renders a 3D perspective grid that scrolls toward the viewer,
 * creating a sense of movement through digital space.
 *
 * Features:
 * - Animated scrolling grid
 * - Perspective depth effect
 * - Cyberpunk color gradient
 * - Horizon glow
 *
 * @param modifier Compose modifier
 */
@Composable
fun DigitalLandscapeBackground(modifier: Modifier = Modifier) {
    // Animation for scrolling effect
    val infiniteTransition = rememberInfiniteTransition(label = "landscape_scroll")

    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val horizonY = height * 0.4f // Horizon line at 40% from top

        // Draw gradient background (space to ground)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Black,
                    Color(0xFF0A0A2E),
                    CyberpunkCyan.copy(alpha = 0.1f)
                ),
                startY = 0f,
                endY = horizonY
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CyberpunkCyan.copy(alpha = 0.1f),
                    Color(0xFF1A0A2E)
                ),
                startY = horizonY,
                endY = height
            )
        )

        // Draw horizon glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    CyberpunkPink.copy(alpha = 0.3f),
                    CyberpunkCyan.copy(alpha = 0.4f),
                    CyberpunkPink.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startY = horizonY - 30f,
                endY = horizonY + 30f
            )
        )

        // Draw perspective grid lines
        val gridSize = 100f
        val numLines = 20

        // Horizontal lines (receding into distance)
        for (i in 0 until numLines) {
            val progress = (i + scrollOffset / gridSize) % numLines / numLines.toFloat()
            val y = horizonY + (height - horizonY) * progress.pow(1.5f)

            // Lines fade as they get closer to horizon
            val alpha = (0.3f + 0.7f * progress).coerceIn(0f, 1f)
            val lineColor = CyberpunkCyan.copy(alpha = alpha * 0.6f)

            // Perspective width (wider at bottom)
            val perspectiveWidth = width * (0.3f + 0.7f * progress)
            val centerX = width / 2f

            drawLine(
                color = lineColor,
                start = Offset(centerX - perspectiveWidth / 2f, y),
                end = Offset(centerX + perspectiveWidth / 2f, y),
                strokeWidth = 2f
            )
        }

        // Vertical lines (converging to center)
        val numVerticalLines = 11 // Odd number for center line
        for (i in 0 until numVerticalLines) {
            val progress = i / (numVerticalLines - 1f)
            val xBottom = width * progress
            val xTop = width / 2f + (xBottom - width / 2f) * 0.3f

            // Gradient along the line (fade toward horizon)
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(xTop, horizonY)
                lineTo(xBottom, height)
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberpunkCyan.copy(alpha = 0.3f),
                        CyberpunkPink.copy(alpha = 0.6f)
                    ),
                    startY = horizonY,
                    endY = height
                ),
                style = Stroke(width = 2f)
            )
        }
    }
}
