package dev.aurakai.collabcanvas.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Data class to hold the state of a pluckable path
 */
data class PluckablePath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    var offset: Offset = Offset.Zero,
    var scale: Float = 1f,
    var alpha: Float = 1f,
    var isPlucked: Boolean = false,
    var targetPosition: Offset = Offset.Zero,
)

/**
 * Composable modifier to add plucking behavior to a Canvas
 */
fun Modifier.pluckableCanvas(
    paths: List<PluckablePath>,
    onPathPlucked: (PluckablePath) -> Unit,
    onPathDropped: (PluckablePath) -> Unit,
    onPathClicked: (PluckablePath) -> Unit,
    onPathMoved: (PluckablePath, Offset) -> Unit,
) = pointerInput(Unit) {
    var currentDrag: PluckablePath? = null

    detectTapGestures(
        onTap = { offset ->
            paths.find { path ->
                path.path.contains(offset.x, offset.y) && !path.isPlucked
            }?.let { path ->
                onPathClicked(path)
            }
        },
        onLongPress = { offset ->
            paths.find { path ->
                path.path.contains(offset.x, offset.y) && !path.isPlucked
            }?.let { path ->
                path.isPlucked = true
                path.targetPosition = offset
                onPathPlucked(path)
                currentDrag = path
            }
        }
    )

    detectDragGestures(
        onDragStart = { offset ->
            paths.find { path ->
                path.path.contains(offset.x, offset.y) && path.isPlucked
            }?.let { path ->
                currentDrag = path
            }
        },
        onDrag = { change, dragAmount ->
            currentDrag?.let { path ->
                val newPosition = path.targetPosition + dragAmount
                path.targetPosition = newPosition
                onPathMoved(path, newPosition)
            }
        },
        onDragEnd = {
            currentDrag?.let { path ->
                onPathDropped(path)
            }
            currentDrag = null
        },
        onDragCancel = {
            currentDrag = null
        }
    )
}

/**
 * Animation spec for the plucking effect
 */
fun pluckAnimationSpec(): AnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

/**
 * Animation spec for the path morphing
 */
fun morphAnimationSpec(): AnimationSpec<Float> = tween(
    durationMillis = 300,
    easing = FastOutSlowInEasing
)

/**
 * Interpolate between two paths
 */
fun interpolatePaths(
    start: Path,
    end: Path,
    progress: Float,
): Path {
    val result = Path()

    // Simple interpolation - in a real app, you'd want to use a more sophisticated algorithm
    // that matches up points between the two paths
    if (progress < 0.5f) {
        result.addPath(start)
    } else {
        result.addPath(end)
    }

    return result
}
