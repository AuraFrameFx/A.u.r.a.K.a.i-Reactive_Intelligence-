package dev.aurakai.collabcanvas.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Represents a drawable element on the collaborative canvas.
 *
 * @property id Unique identifier for the element
 * @property type Type of the canvas element
 * @property path The path data for the element
 * @property color Color of the element
 * @property strokeWidth Width of the stroke in pixels
 * @property zIndex Stacking order of the element (higher values appear on top)
 * @property isSelected Whether the element is currently selected
 * @property createdBy ID of the user who created the element
 * @property createdAt Timestamp when the element was created
 * @property updatedAt Timestamp when the element was last updated
 */
data class CanvasElement(
    val id: String,
    val type: ElementType,
    val path: PathData,
    val color: Color,
    val strokeWidth: Float,
    val zIndex: Int = 0,
    val isSelected: Boolean = false,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    /**
     * Creates a copy of this element with the specified path.
     */
    fun withPath(newPath: PathData): CanvasElement {
        return copy(path = newPath, updatedAt = System.currentTimeMillis())
    }

    /**
     * Creates a copy of this element with the specified selection state.
     */
    fun withSelected(selected: Boolean): CanvasElement {
        return copy(isSelected = selected)
    }

    /**
     * Creates a copy of this element with the specified z-index.
     */
    fun withZIndex(index: Int): CanvasElement {
        return copy(zIndex = index, updatedAt = System.currentTimeMillis())
    }

    /**
     * Creates a copy of this element with the specified color.
     */
    fun withColor(newColor: Color): CanvasElement {
        return copy(color = newColor, updatedAt = System.currentTimeMillis())
    }

    /**
     * Creates a copy of this element with the specified stroke width.
     */
    fun withStrokeWidth(width: Float): CanvasElement {
        return copy(strokeWidth = width, updatedAt = System.currentTimeMillis())
    }

    companion object {
        /**
         * Creates a new CanvasElement with default values.
         */
        fun createDefault(
            id: String,
            createdBy: String,
            path: PathData = PathData(),
            color: Color = Color.Black,
            strokeWidth: Float = 5f,
        ): CanvasElement {
            return CanvasElement(
                id = id,
                type = ElementType.PATH,
                path = path,
                color = color,
                strokeWidth = strokeWidth,
                createdBy = createdBy
            )
        }
    }
}

/**
 * Type of canvas element.
 */
enum class ElementType {
    PATH, // Freeform path
    LINE, // Straight line
    RECTANGLE, // Rectangle
    OVAL, // Circle or oval
    TEXT, // Text element
    IMAGE // Image element
}

/**
 * Data class representing path information that can be serialized.
 */
data class PathData(
    val points: List<Offset> = emptyList(),
    val isComplete: Boolean = false,
) {
    /**
     * Creates a new PathData with an additional point.
     */
    fun addPoint(point: Offset): PathData {
        return copy(points = points + point)
    }

    /**
     * Creates a new PathData marked as complete.
     */
    fun complete(): PathData {
        return copy(isComplete = true)
    }

    /**
     * Converts this PathData to an Android Path object.
     */
    fun toPath(): Path {
        return Path().apply {
            if (points.isNotEmpty()) {
                val first = points.first()
                moveTo(first.x, first.y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
        }
    }
}

/**
 * Type adapter for serializing/deserializing Path objects.
 */
class PathTypeAdapter : JsonSerializer<Path>, JsonDeserializer<Path> {
    override fun serialize(
        src: Path,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        val bounds = android.graphics.RectF()
        src.computeBounds(bounds, true)

        val pathData = src.toPathData()
        val jsonObject = JsonObject()
        jsonObject.addProperty("boundsLeft", bounds.left)
        jsonObject.addProperty("boundsTop", bounds.top)
        jsonObject.addProperty("boundsRight", bounds.right)
        jsonObject.addProperty("boundsBottom", bounds.bottom)
        jsonObject.addProperty("pathData", pathData)
        return jsonObject
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Path {
        val jsonObject = json.asJsonObject
        val pathData = jsonObject.get("pathData").asString
        return Path().apply {
            // Implement path parsing from string representation
            // This is a simplified version - you might need a more robust parser
            pathData.split(" ").forEach { cmd ->
                when (cmd[0]) {
                    'M' -> {
                        val (x, y) = cmd.drop(1).split(",").map { it.toFloat() }
                        moveTo(x, y)
                    }

                    'L' -> {
                        val (x, y) = cmd.drop(1).split(",").map { it.toFloat() }
                        lineTo(x, y)
                    }

                    'Z' -> close()
                }
            }
        }
    }

    private fun Path.toPathData(): String {
        val pathData = StringBuilder()
        val pathIterator = PathIterator()
        pathIterator.iterate(this) { verb, points ->
            when (verb) {
                PathIterator.Verb.MOVE -> {
                    pathData.append("M${points[0].x},${points[0].y} ")
                }

                PathIterator.Verb.LINE -> {
                    pathData.append("L${points[1].x},${points[1].y} ")
                }

                PathIterator.Verb.QUAD -> {
                    pathData.append("Q${points[1].x},${points[1].y} ${points[2].x},${points[2].y} ")
                }

                PathIterator.Verb.CUBIC -> {
                    pathData.append("C${points[1].x},${points[1].y} ${points[2].x},${points[2].y} ${points[3].x},${points[3].y} ")
                }

                PathIterator.Verb.CLOSE -> {
                    pathData.append("Z ")
                }
            }
        }
        return pathData.toString().trim()
    }
}

/**
 * Type adapter for serializing/deserializing Color objects.
 */
class ColorTypeAdapter : JsonSerializer<Color>, JsonDeserializer<Color> {
    override fun serialize(
        src: Color,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        return JsonPrimitive(src.value.toInt())
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Color {
        return Color(json.asLong.toULong())
    }
}
