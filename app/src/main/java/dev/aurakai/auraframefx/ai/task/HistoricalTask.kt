package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.models.AgentType
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a historical task executed by an agent
 */
@Serializable
data class HistoricalTask(
    val agentType: AgentType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun getFormattedTime(): String {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    override fun toString(): String {
        return "[${getFormattedTime()}] ${agentType.name}: $description"
    }
}
