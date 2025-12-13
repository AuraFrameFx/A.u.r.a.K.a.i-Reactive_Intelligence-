package dev.aurakai.collabcanvas.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aurakai.collabcanvas.model.CanvasElement
import dev.aurakai.collabcanvas.model.ElementType
import dev.aurakai.collabcanvas.ui.animation.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue

/**
 * Displays an interactive collaborative drawing canvas with multi-tool support, gesture handling, and animated path rendering.
 *
 * Provides a UI for drawing freehand paths, rectangles, and ovals, with support for pan and zoom gestures, tool selection, and element selection. Users can draw, select, and manipulate elements on the canvas. Includes a toolbar for color and stroke width selection, as well as clear and save actions. Animated transitions are applied to drawn paths for interactive effects.
 */
/**
 * Displays a collaborative drawing canvas with multi-tool support, gesture handling, and animated path rendering.
 *
 * Provides a UI for drawing freehand paths, rectangles, and ovals, including pan and zoom gestures, tool selection, and element selection. Supports animated transitions for interactive path effects and includes clear and save actions.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen() {
    // Canvas state
    val paths = remember { mutableStateListOf<PluckablePath>() }
    val elements = remember { mutableStateListOf<CanvasElement>() }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var strokeWidth by remember { mutableStateOf(5f) }
    var selectedTool by remember { mutableStateOf<ElementType>(ElementType.PATH) }
    var selectedElement by remember { mutableStateOf<CanvasElement?>(null) }
    var isDrawing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    rememberScrollState()

    // Animation states
    val animatedPaths = remember { mutableStateMapOf<Int, PluckablePath>() }
    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    // Update animated paths when paths change
    LaunchedEffect(paths) {
        paths.forEachIndexed { index, path ->
            if (!animatedPaths.containsKey(index)) {
                animatedPaths[index] = path.copy()
            }
        }
    }

    // Canvas gesture handlers
    val panZoomState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        coroutineScope.launch {
            scale.snapTo(scale.value * zoomChange)
            val newOffset = offset.value + panChange * (1f / scale.value)
            offset.snapTo(newOffset)
        }
    }

    val dragState = rememberDraggableState { delta ->
        coroutineScope.launch {
            offset.snapTo(offset.value + Offset(delta, 0f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Co-lab Canvas") },
                actions = {
                    IconButton(onClick = { /* Clear canvas */ }) {
                        Icon(Icons.Default.Delete, "Clear Canvas")
                    }
                    IconButton(onClick = { /* Save canvas */ }) {
                        Icon(Icons.Default.Save, "Save")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tool selection buttons
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.PATH },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.PATH) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.Edit, "Draw")
                }
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.RECTANGLE },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.RECTANGLE) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.CheckBoxOutlineBlank, "Rectangle")
                }
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.OVAL },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.OVAL) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.PanoramaFishEye, "Circle")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isDrawing = true
                            currentPath.moveTo(offset.x, offset.y)
                            tryAwaitRelease()
                            isDrawing = false
                        }
                    )
                }
        ) {
            // Main drawing canvas with transform and drag support
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(panZoomState)
                    .draggable(
                        dragState,
                        orientation = Orientation.Horizontal,
                        onDragStarted = { /* Handle drag start */ },
                        onDragStopped = { /* Handle drag end */ }
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (selectedTool == ElementType.PATH) {
                                    isDrawing = true
                                    currentPath.moveTo(offset.x, offset.y)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                when (selectedTool) {
                                    ElementType.PATH -> {
                                        val x = change.position.x
                                        val y = change.position.y
                                        currentPath.lineTo(x, y)
                                    }

                                    else -> {
                                        // Handle other element types
                                    }
                                }
                            },
                            onDragEnd = {
                                isDrawing = false
                                // Add the completed path to the list
                                if (currentPath.isEmpty.not()) {
                                    paths.add(PluckablePath(currentPath, currentColor, strokeWidth))
                                    currentPath = Path()
                                }
                            }
                        )
                    }
            ) {
                // Draw background grid with transformed coordinates
                withTransform({
                    scale(scale.value, scale.value, pivot = Offset.Zero)
                    translate(offset.x, offset.y)
                }) {
                    drawGrid()
                }

                // Draw all elements with transformed coordinates
                withTransform({
                    scale(scale.value, scale.value, pivot = Offset.Zero)
                    translate(offset.x, offset.y)
                }) {
                    elements.forEach { element ->
                        when (element.type) {
                            ElementType.PATH -> {
                                // Draw path element
                                element.path?.let { path ->
                                    drawPath(
                                        path = path,
                                        color = element.color,
                                        style = Stroke(
                                            width = element.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                            }

                            ElementType.RECTANGLE -> {
                                // Draw selection outline if an element is selected with transformed coordinates
                                selectedElement?.let { element ->
                                    element.bounds?.let { rect ->
                                        withTransform({
                                            scale(scale.value, scale.value, pivot = Offset.Zero)
                                            translate(offset.x, offset.y)
                                        }) {
                                            drawRect(
                                                color = Color.Blue.copy(alpha = 0.5f),
                                                topLeft = Offset(rect.left - 4f, rect.top - 4f),
                                                size = androidx.compose.ui.geometry.Size(
                                                    rect.width + 8f,
                                                    rect.height + 8f
                                                ),
                                                style = Stroke(
                                                    width = 2f / scale.value.coerceAtLeast(
                                                        1f
                                                    )
                                                )
                                            )

                                            // Draw resize handles
                                            val handleSize = 8f / scale.value.coerceAtLeast(1f)

                                            // Top-left
                                            drawCircle(
                                                color = Color.Blue,
                                                radius = handleSize,
                                                center = Offset(rect.left, rect.top)
                                            )

                                            // Top-right
                                            drawCircle(
                                                color = Color.Blue,
                                                radius = handleSize,
                                                center = Offset(rect.right, rect.top)
                                            )

                                            // Bottom-left
                                            drawCircle(
                                                color = Color.Blue,
                                                radius = handleSize,
                                                center = Offset(rect.left, rect.bottom)
                                            )

                                            // Bottom-right
                                            drawCircle(
                                                color = Color.Blue,
                                                radius = handleSize,
                                                center = Offset(rect.right, rect.bottom)
                                            )
                                        }
                                    }
                                }
                                // Draw rectangle
                                element.bounds?.let { rect ->
                                    drawRect(
                                        color = element.color,
                                        topLeft = Offset(rect.left, rect.top),
                                        size = androidx.compose.ui.geometry.Size(
                                            rect.width,
                                            rect.height
                                        ),
                                        style = Stroke(width = element.strokeWidth)
                                    )
                                }
                            }

                            ElementType.OVAL -> {
                                // Draw oval
                                element.bounds?.let { rect ->
                                    drawOval(
                                        color = element.color,
                                        topLeft = Offset(rect.left, rect.top),
                                        size = androidx.compose.ui.geometry.Size(
                                            rect.width,
                                            rect.height
                                        ),
                                        style = Stroke(width = element.strokeWidth)
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }

                // Draw current path being drawn with transformed coordinates
                if (isDrawing) {
                    withTransform({
                        scale(scale.value, scale.value, pivot = Offset.Zero)
                        translate(offset.x, offset.y)
                    }) {
                        drawPath(
                            path = currentPath,
                            color = currentColor,
                            style = Stroke(
                                width = strokeWidth / scale.value,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Draw all paths with animations
                paths.forEachIndexed { index, path ->
                    val animatedPath = animatedPaths[index] ?: return@forEachIndexed

                    // Update animated path properties
                    if (path.isPlucked) {
                        animatedPath.offset = path.offset
                        animatedPath.scale = path.scale
                    } else {
                        // Smoothly return to original position
                        coroutineScope.launch {
                            val anim = remember { Animatable(0f) }
                            anim.animateTo(1f, animationSpec = pluckAnimationSpec())
                            animatedPath.offset = Offset.Zero
                            animatedPath.scale = 1f
                        }
                    }

                    // Draw the path with transformations
                    withTransform({
                        scale(animatedPath.scale, animatedPath.scale, path.path.getBounds().center)
                        translate(animatedPath.offset.x, animatedPath.offset.y)
                    }) {
                        drawPath(
                            path = path.path,
                            color = path.color.copy(alpha = path.alpha),
                            style = Stroke(width = path.strokeWidth)
                        )
                    }
                }
            }

            // Toolbar
            CanvasToolbar(
                onColorSelected = { color ->
                    currentColor = color
                },
                onStrokeWidthSelected = { width ->
                    strokeWidth = width
                },
                onClear = {
                    paths.clear()
                    animatedPaths.clear()
                }
            )
        }
    }

    // Add drawing functionality
    DrawingHandler(
        onPathCreated = { path ->
            paths.add(
                PluckablePath(
                    path = path,
                    color = currentColor,
                    strokeWidth = strokeWidth
                )
            )
        },
        onPathUpdated = { path ->
            if (paths.isNotEmpty()) {
                val lastIndex = paths.lastIndex
                paths[lastIndex] = paths[lastIndex].copy(path = path)
            }
        }
    )
}

@Composable
private fun DrawingHandler(
    onPathCreated: (Path) -> Unit,
    onPathUpdated: (Path) -> Unit,
) {
    var currentPath by remember { mutableStateOf(Path()) }

    LaunchedEffect(Unit) {
        // Initialize drawing
        currentPath = Path()
        onPathCreated(currentPath)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = Path().apply {
                            moveTo(offset.x, offset.y)
                        }
                        onPathCreated(currentPath)
                    },
                    onDrag = { change, _ ->
                        currentPath.lineTo(
                            change.position.x,
                            change.position.y
                        )
                        onPathUpdated(currentPath)
                    },
                    onDragEnd = {
                        // Reset for next drawing
                        currentPath = Path()
                    }
                )
            }
    ) {}
}

// Extension to get bounds of a Path
private fun Path.getBounds(): Rect {
    val bounds = PathBounds()
    this.getBounds(bounds)
    return bounds
}
