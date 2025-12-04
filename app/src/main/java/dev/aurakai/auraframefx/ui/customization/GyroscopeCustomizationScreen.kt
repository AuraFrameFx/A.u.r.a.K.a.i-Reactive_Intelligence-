package dev.aurakai.auraframefx.ui.customization

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkPurple
import kotlin.math.*

/**
 * 3D Gyroscope Customization Editor
 *
 * RPG-style phone customization with:
 * - 3D phone model rotates with gyroscope
 * - AI prompt interface for customizations
 * - Real-time visual preview
 * - Integration with ChromaCore and Theme Engine
 *
 * Tilt your phone to rotate the 3D preview!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyroscopeCustomizationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CustomizationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val customizationState by viewModel.customizationState.collectAsState()
    val rotationAngles by viewModel.rotationAngles.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()

    var promptText by remember { mutableStateOf("") }
    var showPromptBar by remember { mutableStateOf(true) }

    // Start gyroscope on screen load
    LaunchedEffect(Unit) {
        viewModel.startGyroscope()
    }

    // Cleanup gyroscope on dispose
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopGyroscope()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background with stars
        StarField(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "3D CUSTOMIZATION LAB",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "Tilt phone to rotate preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberpunkCyan.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Reset rotation
                    IconButton(onClick = { viewModel.resetRotation() }) {
                        Icon(Icons.Default.RestartAlt, "Reset View", tint = CyberpunkCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A)
                )
            )

            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 3D Phone Preview
                Phone3DPreview(
                    rotationAngles = rotationAngles,
                    customization = customizationState,
                    modifier = Modifier.fillMaxSize()
                )

                // Gyroscope indicator overlay
                GyroscopeIndicator(
                    pitch = rotationAngles.pitch,
                    roll = rotationAngles.roll,
                    yaw = rotationAngles.yaw,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )

                // Current customization info
                CustomizationInfoCard(
                    customization = customizationState,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }

            // AI Prompt Interface
            if (showPromptBar) {
                AIPromptBar(
                    promptText = promptText,
                    onPromptChange = { promptText = it },
                    onSubmit = {
                        if (promptText.isNotBlank()) {
                            viewModel.processAIPrompt(promptText)
                            promptText = ""
                        }
                    },
                    aiResponse = aiResponse,
                    isLoading = customizationState.isProcessing,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 3D Phone Model with Gyroscope Rotation
 */
@Composable
fun Phone3DPreview(
    rotationAngles: RotationAngles,
    customization: CustomizationState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Apply 3D rotation based on gyroscope
        rotate(degrees = rotationAngles.yaw, pivot = Offset(centerX, centerY)) {
            // Phone outline (3D perspective)
            val phoneWidth = 300f
            val phoneHeight = 600f

            // Apply perspective tilt
            val tiltX = rotationAngles.roll * 2f
            val tiltY = rotationAngles.pitch * 2f

            // Phone body with depth
            for (depth in 0..10) {
                val depthOffset = depth * 2f
                val shadowAlpha = 0.1f - (depth * 0.01f)

                drawRoundRect(
                    color = Color.Black.copy(alpha = shadowAlpha),
                    topLeft = Offset(
                        centerX - phoneWidth / 2 + tiltX + depthOffset,
                        centerY - phoneHeight / 2 + tiltY + depthOffset
                    ),
                    size = androidx.compose.ui.geometry.Size(phoneWidth, phoneHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f)
                )
            }

            // Main phone body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        customization.primaryColor.copy(alpha = 0.9f),
                        customization.secondaryColor.copy(alpha = 0.9f)
                    )
                ),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + tiltX,
                    centerY - phoneHeight / 2 + tiltY
                ),
                size = androidx.compose.ui.geometry.Size(phoneWidth, phoneHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f)
            )

            // Holographic glow border
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CyberpunkCyan.copy(alpha = glowPulse),
                        CyberpunkPink.copy(alpha = glowPulse),
                        CyberpunkPurple.copy(alpha = glowPulse),
                        CyberpunkCyan.copy(alpha = glowPulse)
                    )
                ),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + tiltX,
                    centerY - phoneHeight / 2 + tiltY
                ),
                size = androidx.compose.ui.geometry.Size(phoneWidth, phoneHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f),
                style = Stroke(width = 4f)
            )

            // Screen area
            val screenPadding = 20f
            drawRoundRect(
                color = Color(0xFF0A0A0A),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + screenPadding + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + tiltY
                ),
                size = androidx.compose.ui.geometry.Size(
                    phoneWidth - screenPadding * 2,
                    phoneHeight - screenPadding * 2
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f)
            )

            // Status bar
            drawRoundRect(
                color = customization.accentColor.copy(alpha = 0.8f),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + screenPadding + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + tiltY
                ),
                size = androidx.compose.ui.geometry.Size(
                    phoneWidth - screenPadding * 2,
                    40f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f, 30f)
            )

            // Navigation bar
            drawRoundRect(
                color = customization.accentColor.copy(alpha = 0.6f),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + screenPadding + tiltX,
                    centerY + phoneHeight / 2 - screenPadding - 60f + tiltY
                ),
                size = androidx.compose.ui.geometry.Size(
                    phoneWidth - screenPadding * 2,
                    60f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f)
            )

            // Camera punch-hole
            drawCircle(
                color = Color(0xFF0A0A0A),
                radius = 15f,
                center = Offset(
                    centerX + 80f + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + 25f + tiltY
                )
            )
            drawCircle(
                color = CyberpunkCyan.copy(alpha = 0.5f),
                radius = 15f,
                center = Offset(
                    centerX + 80f + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + 25f + tiltY
                ),
                style = Stroke(width = 1f)
            )
        }
    }
}

/**
 * Gyroscope Rotation Indicator
 */
@Composable
fun GyroscopeIndicator(
    pitch: Float,
    roll: Float,
    yaw: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkCyan.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.RotateRight,
                    null,
                    tint = CyberpunkCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "GYROSCOPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberpunkCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "Pitch: ${pitch.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                "Roll: ${roll.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                "Yaw: ${yaw.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Current Customization Info
 */
@Composable
fun CustomizationInfoCard(
    customization: CustomizationState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkPink.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Palette,
                    null,
                    tint = CyberpunkPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ACTIVE THEME",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberpunkPink,
                    fontWeight = FontWeight.Bold
                )
            }

            // Color swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ColorSwatch(customization.primaryColor, "Primary")
                ColorSwatch(customization.secondaryColor, "Secondary")
                ColorSwatch(customization.accentColor, "Accent")
            }

            if (customization.description.isNotBlank()) {
                Text(
                    customization.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ColorSwatch(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(50.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, RoundedCornerShape(8.dp))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 8.sp
        )
    }
}

/**
 * AI Prompt Interface
 */
@Composable
fun AIPromptBar(
    promptText: String,
    onPromptChange: (String) -> Unit,
    onSubmit: () -> Unit,
    aiResponse: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A)
        ),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // AI Response
            if (aiResponse != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = CyberpunkCyan.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            tint = CyberpunkCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            aiResponse,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Prompt input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptText,
                    onValueChange = onPromptChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask AI: 'Make it cyberpunk pink', 'Dark neon theme', etc...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Psychology, "AI", tint = CyberpunkPurple)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberpunkPurple,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = onSubmit,
                    enabled = !isLoading && promptText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberpunkPurple
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, "Apply")
                    }
                }
            }

            // Quick prompts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPromptChip("Cyberpunk", onClick = { onPromptChange("Cyberpunk neon theme") })
                QuickPromptChip("Dark Mode", onClick = { onPromptChange("Dark theme with purple accents") })
                QuickPromptChip("Minimal", onClick = { onPromptChange("Minimal white and gray theme") })
            }
        }
    }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFF1A1A1A),
            labelColor = CyberpunkCyan
        ),
        border = AssistChipDefaults.assistChipBorder(
            borderColor = CyberpunkCyan.copy(alpha = 0.5f)
        )
    )
}

/**
 * Animated starfield background
 */
@Composable
fun StarField(modifier: Modifier = Modifier) {
    val stars = remember {
        List(100) {
            Star(
                x = kotlin.random.Random.nextFloat(),
                y = kotlin.random.Random.nextFloat(),
                size = kotlin.random.Random.nextFloat() * 2f + 1f,
                twinkleSpeed = kotlin.random.Random.nextFloat() * 2f + 1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier) {
        stars.forEach { star ->
            val alpha = (sin(time * PI.toFloat() * star.twinkleSpeed) + 1f) / 2f
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.6f),
                radius = star.size,
                center = Offset(
                    star.x * size.width,
                    star.y * size.height
                )
            )
        }
    }
}

data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleSpeed: Float
)
