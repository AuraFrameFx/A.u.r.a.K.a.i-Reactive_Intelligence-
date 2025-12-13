package dev.aurakai.collabcanvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CanvasToolbar(
    onColorSelected: (Color) -> Unit,
    onStrokeWidthSelected: (Float) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showStrokeWidthPicker by remember { mutableStateOf(false) }

    val colors = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan
    )

    val strokeWidths = listOf(1f, 3f, 5f, 8f, 12f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Color Picker Button
        IconButton(
            onClick = { showColorPicker = !showColorPicker }
        ) {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = "Select color",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Stroke Width Button
        IconButton(
            onClick = { showStrokeWidthPicker = !showStrokeWidthPicker }
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = "Select stroke width",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Clear Canvas Button
        IconButton(
            onClick = onClear
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear canvas",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

    // Color Picker Dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Select Color") },
            text = {
                Column {
                    colors.chunked(4).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable {
                                            onColorSelected(color)
                                            showColorPicker = false
                                        }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showColorPicker = false }
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Stroke Width Picker Dialog
    if (showStrokeWidthPicker) {
        AlertDialog(
            onDismissRequest = { showStrokeWidthPicker = false },
            title = { Text("Select Stroke Width") },
            text = {
                Column {
                    strokeWidths.forEach { width ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    onStrokeWidthSelected(width)
                                    showStrokeWidthPicker = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(width.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showStrokeWidthPicker = false }
                ) {
                    Text("Close")
                }
            }
        )
    }
}
