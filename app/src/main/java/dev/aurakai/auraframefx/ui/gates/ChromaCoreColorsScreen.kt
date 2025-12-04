package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.colorblendr.ColorBlendr

/**
 * ChromaCore Colors Screen - Pure color customization interface
 *
 * This screen provides access to Material 3 color scheme customization
 * with live preview. COLORS ONLY - no typography, shapes, or other theme elements.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaCoreColorsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // Color scheme state
    var primaryColor by remember { mutableStateOf(colorScheme.primary) }
    var secondaryColor by remember { mutableStateOf(colorScheme.secondary) }
    var tertiaryColor by remember { mutableStateOf(colorScheme.tertiary) }
    var backgroundColor by remember { mutableStateOf(colorScheme.background) }
    var surfaceColor by remember { mutableStateOf(colorScheme.surface) }

    // Preview state
    var showPreview by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ChromaCore",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.Cyan
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Material 3 Color Scheme",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Customize your app's color palette. Colors only - no other theme elements.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Primary Color
                item {
                    ColorRoleCard(
                        title = "Primary",
                        description = "Main brand color",
                        color = primaryColor,
                        onColorChange = { primaryColor = it }
                    )
                }

                // Secondary Color
                item {
                    ColorRoleCard(
                        title = "Secondary",
                        description = "Accent color for less prominent elements",
                        color = secondaryColor,
                        onColorChange = { secondaryColor = it }
                    )
                }

                // Tertiary Color
                item {
                    ColorRoleCard(
                        title = "Tertiary",
                        description = "Contrasting accent color",
                        color = tertiaryColor,
                        onColorChange = { tertiaryColor = it }
                    )
                }

                // Background Color
                item {
                    ColorRoleCard(
                        title = "Background",
                        description = "Main background color",
                        color = backgroundColor,
                        onColorChange = { backgroundColor = it }
                    )
                }

                // Surface Color
                item {
                    ColorRoleCard(
                        title = "Surface",
                        description = "Color of cards and elevated surfaces",
                        color = surfaceColor,
                        onColorChange = { surfaceColor = it }
                    )
                }

                // Live Preview Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Live Preview",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ColorPreviewCard(
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        backgroundColor = backgroundColor,
                        surfaceColor = surfaceColor
                    )
                }

                // Action Buttons
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Reset to default colors
                                primaryColor = Color(0xFF00BFFF)
                                secondaryColor = Color(0xFF00FFFF)
                                tertiaryColor = Color(0xFF9C27B0)
                                backgroundColor = Color.Black
                                surfaceColor = Color(0xFF1A1A1A)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Cyan
                            )
                        ) {
                            Text("Reset")
                        }

                        Button(
                            onClick = {
                                // TODO: Save color scheme to preferences
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Cyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Apply", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ColorRoleCard(
    title: String,
    description: String,
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }

                // Color preview box
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(color, RoundedCornerShape(8.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                )
            }

            // Color picker sliders
            ColorSliders(
                color = color,
                onColorChange = onColorChange
            )
        }
    }
}

@Composable
private fun ColorSliders(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Red slider
        ColorSlider(
            label = "Red",
            value = color.red,
            onValueChange = { red ->
                onColorChange(color.copy(red = red))
            },
            color = Color.Red
        )

        // Green slider
        ColorSlider(
            label = "Green",
            value = color.green,
            onValueChange = { green ->
                onColorChange(color.copy(green = green))
            },
            color = Color.Green
        )

        // Blue slider
        ColorSlider(
            label = "Blue",
            value = color.blue,
            onValueChange = { blue ->
                onColorChange(color.copy(blue = blue))
            },
            color = Color.Blue
        )
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f)
            ),
            modifier = Modifier.width(50.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f)
            )
        )

        Text(
            text = "${(value * 255).toInt()}",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f)
            ),
            modifier = Modifier.width(40.dp)
        )
    }
}

@Composable
private fun ColorPreviewCard(
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    backgroundColor: Color,
    surfaceColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preview Sample UI",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            // Sample card with surface color
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sample Card",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White
                        )
                    )

                    // Primary button
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Primary Action")
                    }

                    // Secondary button
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = secondaryColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Secondary Action")
                    }

                    // Tertiary accent
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(tertiaryColor, RoundedCornerShape(4.dp))
                        )
                        Column {
                            Text(
                                text = "Tertiary Accent",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Used for contrast",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
