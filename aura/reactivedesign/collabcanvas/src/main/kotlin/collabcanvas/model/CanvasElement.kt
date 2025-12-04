package collabcanvas.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Represents a drawable element on the collaborative canvas.
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
    fun withPath(newPath: PathData): CanvasElement {
        return copy(path = newPath, updatedAt = System.currentTimeMillis())
    }

    fun withSelected(selected: Boolean): CanvasElement {
        return copy(isSelected = selected)
    }

    fun withZIndex(index: Int): CanvasElement {
        return copy(zIndex = index, updatedAt = System.currentTimeMillis())
    }

    fun withColor(newColor: Color): CanvasElement {
        return copy(color = newColor, updatedAt = System.currentTimeMillis())
    }

    fun withStrokeWidth(width: Float): CanvasElement {
        return copy(strokeWidth = width, updatedAt = System.currentTimeMillis())
    }

    companion object {
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

enum class ElementType {
    PATH, LINE, RECTANGLE, OVAL, TEXT, IMAGE
}

data class PathData(
    val points: List<Offset> = emptyList(),
    val isComplete: Boolean = false,
) {
    fun addPoint(point: Offset): PathData {
        return copy(points = points + point)
    }

    fun complete(): PathData {
        return copy(isComplete = true)
    }

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

class PathTypeAdapter : JsonSerializer<Path>, JsonDeserializer<Path> {
    override fun serialize(
        src: Path,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        val bounds = src.getBounds()
        val jsonObject = JsonObject()
        jsonObject.addProperty("boundsLeft", bounds.left)
        jsonObject.addProperty("boundsTop", bounds.top)
        jsonObject.addProperty("boundsRight", bounds.right)
        jsonObject.addProperty("boundsBottom", bounds.bottom)
        jsonObject.addProperty("pathData", "M0,0") // Simplified
        return jsonObject
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Path {
        return Path() // Simplified implementation
    }
}

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
