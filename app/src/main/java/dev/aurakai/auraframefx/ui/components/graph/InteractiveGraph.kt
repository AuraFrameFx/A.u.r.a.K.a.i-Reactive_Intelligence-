package dev.aurakai.auraframefx.ui.components.graph

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.ui.geometry.Offset as ComposeOffset

/**
 * Displays an interactive, zoomable, and pannable graph visualization with selectable nodes.
 *
 * Renders a graph of nodes and their connections on a canvas, supporting pinch-to-zoom and pan gestures.
 * Nodes can be selected, triggering a pulsing animation effect. Connections are drawn with visual styles
 * based on their type, and node labels are displayed below each node. The graph content is centered within
 * the available space, and a grid background is rendered behind the graph.
 *
 * @param nodes The list of graph nodes to display, each with position and connection data.
 * @param selectedNodeId The ID of the currently selected node, if any.
 * @param onNodeSelected Callback invoked when a node is selected, receiving the node's ID.
 * @param modifier Modifier to be applied to the graph container.
 * @param contentPadding Padding to apply around the graph content.
 */
@Composable
fun InteractiveGraph(
    nodes: List<GraphNode>,
    modifier: Modifier = Modifier,
    selectedNodeId: String? = null,
    onNodeSelected: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    var scale by remember { mutableStateOf(1f) }
    var translation by remember { mutableStateOf(ComposeOffset.Zero) }
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            .clip(MaterialTheme.shapes.medium)
    ) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        // Calculate content bounds for centering
        val contentWidth = 1000f * scale
        val contentHeight = 800f * scale

        val offsetX = (canvasWidth - contentWidth) / 2 + translation.x
        val offsetY = (canvasHeight - contentHeight) / 2 + translation.y

        val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        val nodeTextColor = Color.White // Or MaterialTheme.colorScheme.onPrimary if appropriate

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            translation += pan / scale
                        }
                    )
                }
        ) {
            // Draw grid
            drawGrid(scale, translation, gridColor)

            // Draw connections first (behind nodes)
            nodes.forEach { node ->
                node.connections.forEach { connection ->
                    val targetNode = nodes.find { it.id == connection.targetId }
                    targetNode?.let { drawConnection(node, it, connection) }
                }
            }

            // Draw nodes
            nodes.forEach { node ->
                val isSelected = node.id == selectedNodeId
                val nodeScale = if (isSelected) pulse else 1f
                val currentOffset = ComposeOffset(offsetX, offsetY) + node.position.toCompose() * scale

                withTransform({
                    translate(
                        left = currentOffset.x - node.position.toCompose().x * scale * nodeScale,
                        top = currentOffset.y - node.position.toCompose().y * scale * nodeScale
                    )
                    scale(scale * nodeScale, scale * nodeScale, pivot = node.position.toCompose())
                }) {
                    drawNode(node, isSelected, nodeTextColor, this)
                }
            }
        }
    }
}

private fun DrawScope.drawGrid(scale: Float, translation: ComposeOffset, gridColor: Color) {
    val gridSize = 40f * scale // Adjust grid size with scale
    val strokeWidth = (1f / scale).coerceAtLeast(0.5f) // Ensure minimum stroke width

    var x = translation.x % gridSize
    while (x < size.width) {
        drawLine(
            color = gridColor,
            start = ComposeOffset(x, 0f),
            end = ComposeOffset(x, size.height),
            strokeWidth = strokeWidth
        )
        x += gridSize
    }

    var y = translation.y % gridSize
    while (y < size.height) {
        drawLine(
            color = gridColor,
            start = ComposeOffset(0f, y),
            end = ComposeOffset(size.width, y),
            strokeWidth = strokeWidth
        )
        y += gridSize
    }
}

private fun drawNode(node: GraphNode, isSelected: Boolean, textColor: Color, drawScope: DrawScope) {
    with(drawScope) {
        val nodeSize = node.type.defaultSize.toPx()
        val center = node.position.toCompose() // Use the toCompose() extension

        // Draw glow/selection ring
        if (isSelected) {
            val ringWidth = 4.dp.toPx()
            drawCircle(
                color = node.type.color.copy(alpha = 0.5f),
                radius = nodeSize * 0.7f,
                center = center,
                style = Stroke(width = ringWidth * 2)
            )
        }

        // Draw node background
        drawCircle(
            color = node.type.color.copy(alpha = 0.2f),
            radius = nodeSize * 0.6f,
            center = center
        )

        // Draw node border
        drawCircle(
            color = node.type.color,
            radius = nodeSize * 0.6f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw node icon background
        val iconSize = nodeSize * 0.5f
        val iconBgRadius = iconSize * 0.8f

        // Draw icon background circle
        drawCircle(
            color = node.type.color,
            radius = iconBgRadius,
            center = center
        )

        // Draw the icon (simplified - actual icon rendering would require more complex handling)
        drawCircle(
            color = Color.White,
            radius = iconBgRadius * 0.5f,
            center = center
        )

        // Draw node label
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                node.name,
                center.x,
                center.y + nodeSize * 0.8f + 12.dp.toPx(), // Adjusted Y for better placement
                android.graphics.Paint().apply {
                    color = textColor.toArgb() // Use passed textColor
                    textSize = 12.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

private fun DrawScope.drawConnection(
    from: GraphNode,
    to: GraphNode,
    connection: Connection,
) {
    val fromCenter = from.position.toCompose()
    val toCenter = to.position.toCompose()
    val direction = ComposeOffset(toCenter.x - fromCenter.x, toCenter.y - fromCenter.y)
    val dx = direction.x
    val dy = direction.y
    val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    if (distance == 0f) return // Avoid division by zero if nodes are at the same position
    val directionNormalized = ComposeOffset(direction.x / distance, direction.y / distance)

    val fromRadius = from.type.defaultSize.toPx() * 0.6f
    val toRadius = to.type.defaultSize.toPx() * 0.6f

    val start = ComposeOffset(fromCenter.x + directionNormalized.x * fromRadius, fromCenter.y + directionNormalized.y * fromRadius)
    val end = ComposeOffset(toCenter.x - directionNormalized.x * toRadius, toCenter.y - directionNormalized.y * toRadius)

    // Draw connection line
    val strokeWidth = 2.dp.toPx()
    val color = when (connection.type) {
        ConnectionType.DIRECT -> Color.White.copy(alpha = 0.7f)
        ConnectionType.BIDIRECTIONAL -> Color.Green.copy(alpha = 0.7f)
        ConnectionType.DASHED -> Color.Yellow.copy(alpha = 0.7f)
    }

    if (connection.type == ConnectionType.DASHED) {
        // Draw dashed line
        val dashLength = 10.dp.toPx()
        val gapLength = 5.dp.toPx()
        val lineLength = distance - fromRadius - toRadius
        if (lineLength <= 0) return // No space to draw the line

        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        drawLine(
            color = color,
            start = start, // Use the calculated start and end for the dashed line
            end = end,
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
    } else {
        // Draw solid line
        drawLine(
            color = color,
            start = start,
            end = end,
            strokeWidth = strokeWidth
        )
    }

    // Draw arrow head
    if (connection.type == ConnectionType.DIRECT || connection.type == ConnectionType.BIDIRECTIONAL) {
        val arrowSize = 10.dp.toPx()
        val arrowAngle = (PI / 6).toFloat() // 30 degrees for a narrower arrow

        // Arrowhead for 'to' node
        drawArrowHead(end, directionNormalized, arrowSize, arrowAngle, color)

        // Arrowhead for 'from' node if bidirectional
        if (connection.type == ConnectionType.BIDIRECTIONAL) {
            drawArrowHead(start, ComposeOffset(-directionNormalized.x, -directionNormalized.y), arrowSize, arrowAngle, color)
        }
    }
}

private fun DrawScope.drawArrowHead(
    tip: ComposeOffset,
    direction: ComposeOffset,
    size: Float,
    angle: Float,
    color: Color,
) {
    val arrowPath = Path().apply {
        val p1 = ComposeOffset(tip.x - (direction.rotate(angle).x * size), tip.y - (direction.rotate(angle).y * size))
        val p2 = ComposeOffset(tip.x - (direction.rotate(-angle).x * size), tip.y - (direction.rotate(-angle).y * size))
        moveTo(tip.x, tip.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        close()
    }
    drawPath(path = arrowPath, color = color)
}

// Extension to convert project Offset (GraphNode.Offset) to ComposeOffset
fun Offset.toCompose(): ComposeOffset = ComposeOffset(x = this.x, y = this.y)

/**
 * Rotates this offset by the given angle in radians.
 */
fun ComposeOffset.rotate(angle: Float): ComposeOffset {
    val cosAngle = cos(angle)
    val sinAngle = sin(angle)
    return ComposeOffset(x * cosAngle - y * sinAngle, x * sinAngle + y * cosAngle)
}
