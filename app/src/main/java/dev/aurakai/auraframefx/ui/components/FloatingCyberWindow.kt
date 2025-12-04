package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPurple

/**
 * Displays a floating window with a customizable cyber-themed appearance.
 *
 * Features:
 * - Glowing animated border
 * - Cyberpunk-themed header with title
 * - Customizable corner typography and background
 * - Elevated shadow for floating effect
 * - Gradient accents
 *
 * @param modifier Compose modifier
 * @param title The title displayed in the window's header
 * @param cornerStyle The typography of the window's corners
 * @param backgroundStyle The background typography of the window
 * @param content The composable content to display inside the window
 */
@Composable
fun FloatingCyberWindow(
    modifier: Modifier = Modifier,
    title: String,
    cornerStyle: CornerStyle = CornerStyle.ROUNDED,
    backgroundStyle: BackgroundStyle = BackgroundStyle.SOLID,
    content: @Composable () -> Unit,
) {
    // Animation for border glow
    val infiniteTransition = rememberInfiniteTransition(label = "window_glow")

    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_glow"
    )

    // Determine shape based on corner style
    val shape = when (cornerStyle) {
        CornerStyle.ROUNDED -> RoundedCornerShape(16.dp)
        CornerStyle.SHARP -> RoundedCornerShape(4.dp)
        CornerStyle.CLIPPED -> RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 4.dp,
            bottomStart = 4.dp,
            bottomEnd = 16.dp
        )
        CornerStyle.HEXAGON -> RoundedCornerShape(8.dp)
        CornerStyle.ANGLED -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 0.dp
        )
    }

    // Determine background based on style
    val background = when (backgroundStyle) {
        BackgroundStyle.SOLID -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0A0A2E),
                Color(0xFF1A0A3E)
            )
        )
        BackgroundStyle.GLASS -> Brush.verticalGradient(
            colors = listOf(
                Color(0x40FFFFFF),
                Color(0x20FFFFFF)
            )
        )
        BackgroundStyle.GRADIENT -> Brush.verticalGradient(
            colors = listOf(
                CyberpunkCyan.copy(alpha = 0.2f),
                CyberpunkPink.copy(alpha = 0.2f)
            )
        )
        BackgroundStyle.GLITCH -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A0A2E),
                Color(0xFF2A1A4E)
            )
        )
        BackgroundStyle.MATRIX -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF001A00),
                Color(0xFF002200)
            )
        )
        BackgroundStyle.HEX_GRID -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0A1A3E),
                Color(0xFF1A2A4E)
            )
        )
        BackgroundStyle.TRANSPARENT -> Brush.verticalGradient(
            colors = listOf(
                Color(0x200A0A2E),
                Color(0x201A0A3E)
            )
        )
    }

    Card(
        modifier = modifier
            .shadow(
                elevation = 24.dp,
                shape = shape,
                ambientColor = CyberpunkCyan,
                spotColor = CyberpunkPink
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        CyberpunkCyan.copy(alpha = borderAlpha),
                        CyberpunkPink.copy(alpha = borderAlpha),
                        NeonPurple.copy(alpha = borderAlpha)
                    )
                ),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    CyberpunkCyan.copy(alpha = 0.3f),
                                    CyberpunkPink.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = title,
                        color = NeonBlue,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    // Corner accent indicators
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(8.dp)
                            .background(CyberpunkCyan, shape = RoundedCornerShape(4.dp))
                    )
                }

                // Content area with padding
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
