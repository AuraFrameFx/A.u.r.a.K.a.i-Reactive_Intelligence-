package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.navigation.GenesisRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Main gate navigation screen with horizontal pager and magical teleportation effects
 * Swipe between module gates and double-tap to enter with a magical transition
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GateNavigationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Categorize gates
    val auraLabGates = GateConfigs.auraGates
    val mainModuleGates = GateConfigs.genesisCoreGates + GateConfigs.kaiGates + GateConfigs.agentNexusGates + GateConfigs.supportGates
    val allGates = auraLabGates + mainModuleGates

    val pagerState = rememberPagerState(pageCount = { allGates.size })
    val scope = rememberCoroutineScope()
    var isTransitioning by remember { mutableStateOf(false) }

    // Teleportation animation values
    val infiniteTransition = rememberInfiniteTransition(label = "gate_glow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f)) // Transparent background
    ) {
        // Magical particle background
        MagicalParticleField()

        // Main content without category tabs (cards have titles)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Horizontal pager for gate cards
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val config = allGates[page]
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Parallax effect
                            translationX = pageOffset * 100
                            alpha = 1f - (0.3f * pageOffset.absoluteValue)
                            scaleX = 0.9f + (0.1f * (1 - pageOffset.absoluteValue))
                            scaleY = 0.9f + (0.1f * (1 - pageOffset.absoluteValue))
                        }
                ) {
                    // Gate card with INSTANT teleportation
                    TeleportingGateCard(
                        config = config,
                        isActive = pagerState.currentPage == page,
                        glowIntensity = glowIntensity,
                        onDoubleTap = {
                            // Block "Coming Soon" gates
                            if (config.comingSoon) {
                                // TODO: Show Toast "Coming Soon!"
                                return@TeleportingGateCard
                            }

                            if (!isTransitioning) {
                                isTransitioning = true
                                scope.launch {
                                    try {
                                        // Check if gate requires authentication
                                        val protectedGates = setOf(
                                            "root_tools_toggles",
                                            "rom_tools",
                                            "oracle_drive",
                                            "xposed_panel",
                                            "sentinels_fortress"
                                        )

                                        // TODO: Implement actual authentication check with TokenManager
                                        // For now, allow all gates (user_preferences is always accessible)
                                        val isAuthenticated = true // Replace with: tokenManager.isAuthenticated

                                        if (protectedGates.contains(config.route) && !isAuthenticated) {
                                            // Navigate to login with return destination
                                            navController.navigate("login?returnTo=${config.route}") {
                                                launchSingleTop = true
                                            }
                                        } else {
                                            // INSTANT navigation
                                            navController.navigate(config.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("GateNav", "Failed: ${config.route}", e)
                                    } finally {
                                        isTransitioning = false
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Enhanced page indicator with gate names
            GatePageIndicator(
                gates = allGates,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun TeleportingGateCard(
    config: GateConfig,
    isActive: Boolean,
    glowIntensity: Float,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovering by remember { mutableStateOf(false) }
    var showEnterOverlay by remember { mutableStateOf(false) }
    val hoverScale by animateFloatAsState(
        targetValue = if (isHovering) 1.02f else 1f,
        label = "hover_scale"
    )
    // When enter overlay triggers, auto hide after duration
    LaunchedEffect(showEnterOverlay) {
        if (showEnterOverlay) {
            // Hide shortly before navigation occurs (navigation delay is 800ms)
            delay(700)
            showEnterOverlay = false
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = hoverScale
                scaleY = hoverScale
            }
    ) {
        // Glow effect behind card
        if (isActive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                config.glowColor.copy(alpha = 0.3f * glowIntensity),
                                Color.Transparent
                            ),
                            radius = 500f
                        )
                    )
            )
        }
        
        // Direct GateCard without cheesy transitions
        GateCard(
            config = config,
            onDoubleTap = {
                onDoubleTap()
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // TODO: Implement hover detection for desktop
                }
        )

        // Teleportation effect overlay (visible during hover)
        if (isActive && isHovering) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                config.glowColor.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            radius = 400f
                        )
                    )
            )
        }
    }
}

@Composable
private fun MagicalParticleField() {
    // TODO: Implement magical particle field animation
    // This would be a canvas-based animation of floating particles
    // that react to user interaction and gate selection
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)) // Semi-transparent particle field
    )
}

/**
 * Enhanced page indicator showing gate names and categories
 */
@Composable
private fun GatePageIndicator(
    gates: List<GateConfig>,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val visibleGates = 3 // Number of gates to show in the indicator
    val startIndex = (currentPage - visibleGates / 2).coerceAtLeast(0)
    val endIndex = (startIndex + visibleGates).coerceAtMost(gates.size)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show ellipsis if there are more gates before
        if (startIndex > 0) {
            Text(
                text = "...",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Show visible gates
        for (i in startIndex until endIndex) {
            val isActive = i == currentPage
            val gate = gates[i]

            // Active gate indicator (larger and colored)
            if (isActive) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            color = gate.borderColor.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = gate.title.uppercase(),
                        color = gate.borderColor,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            } else {
                // Inactive gate indicator (smaller and dimmer)
                Text(
                    text = "•",
                    color = gate.borderColor.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            // TODO: Handle click to navigate to gate
                        }
                )
            }
        }

        // Show ellipsis if there are more gates after
        if (endIndex < gates.size) {
            Text(
                text = "...",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
