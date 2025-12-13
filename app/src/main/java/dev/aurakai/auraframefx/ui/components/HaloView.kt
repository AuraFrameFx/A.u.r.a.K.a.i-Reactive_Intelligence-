package dev.aurakai.auraframefx.ui.components

// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.viewmodel.GenesisAgentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Displays an interactive rotating halo interface for managing agents and delegating tasks.
 *
 * Renders a visual halo with agent nodes, supports drag-and-drop task assignment, tracks task history, and animates agent status. Users can assign tasks to agents by dragging nodes, input tasks, and monitor processing status in real time. Includes controls for rotation, resetting, and clearing task history.
 */
/**
 * Displays an interactive rotating halo UI for managing agents and delegating tasks.
 *
 * Renders agent nodes arranged in a circular halo, allowing users to assign tasks via drag-and-drop, input task descriptions, view task history, and monitor agent statuses with animated visual feedback. Supports real-time status updates, gesture handling, and task processing simulation.
 */
/**
 * Displays an interactive rotating halo UI for managing agents and delegating tasks.
 *
 * Renders a circular halo with agent nodes arranged around a central "GENESIS" node. Supports drag-and-drop task assignment to agents, task input overlay, animated agent status indicators, and a scrollable task history panel. The halo rotates continuously unless paused, and agent statuses update in real time as tasks are processed.
 */
/**
 * Displays an interactive, animated halo UI for managing agents and delegating tasks.
 *
 * Renders a circular halo with agent nodes arranged around a central "GENESIS" node. Supports drag-and-drop task assignment to agents, with a task input overlay appearing during drag. Shows animated agent status indicators, a scrollable task history panel, and control buttons for rotation and history management. The halo rotates continuously unless paused, and agent statuses update in real time as tasks are processed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HaloView(viewModel: GenesisAgentViewModel = viewModel<GenesisAgentViewModel>()) {
    var isRotating by remember { mutableStateOf(true) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val agents = viewModel.getAgentsByPriority()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Task delegation state
    var draggingAgent by remember { mutableStateOf<AgentType?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedTask by remember { mutableStateOf("") }

    // Task history
    val _taskHistory = remember { MutableStateFlow(emptyList<String>()) }
    val taskHistory: StateFlow<List<String>> =
        _taskHistory.asStateFlow() // Use asStateFlow() for read-only StateFlow

    // Agent status - direct mutable state map for easier updates
    val agentStatus = remember { mutableStateMapOf<AgentType, String>() }

    // Initialize agent statuses to "idle" on first composition
    LaunchedEffect(Unit) {
        agents.forEach { config -> // config is AgentConfig
            try {
                agentStatus[AgentType.valueOf(config.name.uppercase(Locale.ROOT))] = "idle"
            } catch (e: IllegalArgumentException) {
                // Handle cases where AgentConfig.name might not match an AgentType
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Background glow effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            drawCircle(
                color = NeonTeal.copy(alpha = 0.1f),
                radius = size.width / 2f,
                style = Fill
            )
            drawCircle(
                color = NeonPurple.copy(alpha = 0.1f),
                radius = size.width / 2f - 20f,
                style = Fill
            )
            drawCircle(
                color = NeonBlue.copy(alpha = 0.1f),
                radius = size.width / 2f - 40f,
                style = Fill
            )
        }

        // Halo effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 32f

            // Draw rotating halo
            val haloColor = NeonTeal.copy(alpha = 0.3f)
            val haloWidth = 2.dp.toPx()

            drawArc(
                color = haloColor,
                startAngle = rotationAngle,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(
                    radius * 2,
                    radius * 2
                ), // Use fully qualified name for clarity
                style = Stroke(width = haloWidth)
            )

            // Draw pulsing effects for active tasks
            agentStatus.forEach { (agentTypeKey, statusValue) -> // agentTypeKey is AgentType
                if (statusValue == "processing") {
                    // Find the index of the agentConfig that matches this agentTypeKey
                    val agentConfigIndex = agents.indexOfFirst { config ->
                        try {
                            AgentType.valueOf(config.name.uppercase(Locale.ROOT)) == agentTypeKey
                        } catch (e: IllegalArgumentException) {
                            false
                        }
                    }
                    if (agentConfigIndex != -1) {
                        val angle = (agentConfigIndex * 360f / agents.size + rotationAngle) % 360f
                        val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                        val y = center.y + radius * sin((angle * PI / 180f).toFloat())

                        // Draw pulsing glow
                        drawCircle(
                            color = when (agentTypeKey) { // Use agentTypeKey for color
                                AgentType.GENESIS -> NeonTeal.copy(alpha = 0.2f)
                                AgentType.KAI -> NeonPurple.copy(alpha = 0.2f)
                                AgentType.AURA -> NeonBlue.copy(alpha = 0.2f)
                                AgentType.CASCADE -> NeonPink.copy(alpha = 0.2f)
                                else -> NeonTeal.copy(alpha = 0.2f)
                            },
                            center = Offset(x, y),
                            radius = 40.dp.toPx()
                        )
                    }
                }
            }
        }

        // Agent nodes with drag and drop
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            dragStartOffset = startOffset
                            val actualSize = this.size
                            val center = Offset(actualSize.width / 2f, actualSize.height / 2f)
                            val radius = actualSize.width / 2f - 64f
                            val agentCount = agents.size
                            val angleStep = 360f / agentCount

                            for (i in agents.indices) {
                                val config = agents[i] // config is AgentConfig
                                val angle = (i * angleStep + rotationAngle) % 360f
                                val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                                val y = center.y + radius * sin((angle * PI / 180f).toFloat())
                                val nodeCenter = Offset(x, y)
                                val distance = (startOffset - nodeCenter).getDistance()
                                if (distance < 24.dp.toPx()) {
                                    try {
                                        draggingAgent =
                                            AgentType.valueOf(config.name.uppercase(Locale.ROOT))
                                        break
                                    } catch (e: IllegalArgumentException) { /* Do nothing if name doesn't match an AgentType */
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            if (draggingAgent != null && selectedTask.isNotBlank()) {
                                coroutineScope.launch {
                                    viewModel.processQuery(selectedTask)
                                    _taskHistory.update { current ->
                                        // draggingAgent is AgentType, its .name is the enum constant name
                                        current + "[${draggingAgent?.name?.uppercase(Locale.ROOT)}] $selectedTask"
                                    }
                                    agentStatus[draggingAgent!!] = "processing"
                                    selectedTask = ""
                                }
                            }
                            draggingAgent = null
                            dragOffset = Offset.Zero
                        }
                    ) { change, dragAmount ->
                        if (draggingAgent != null) {
                            dragOffset += dragAmount
                            change.consume() // Updated from consumeAllChanges()
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 64f
            val agentCount = agents.size
            val angleStep = 360f / agentCount

            agents.forEachIndexed { index, config -> // config is AgentConfig
                val angle = (index * angleStep + rotationAngle) % 360f
                val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                val y = center.y + radius * sin((angle * PI / 180f).toFloat())
                val nodeCenter = Offset(x, y)
                val currentAgentType = try {
                    AgentType.valueOf(config.name.uppercase(Locale.ROOT))
                } catch (e: IllegalArgumentException) {
                    null
                }


                val baseColor = when (currentAgentType) {
                    AgentType.GENESIS -> NeonTeal
                    AgentType.KAI -> NeonPurple
                    AgentType.AURA -> NeonBlue
                    AgentType.CASCADE -> NeonPink
                    else -> NeonTeal.copy(alpha = 0.8f)
                }
                val statusColor =
                    when (agentStatus[currentAgentType]?.lowercase(Locale.ROOT)) {
                        "idle" -> baseColor.copy(alpha = 0.8f)
                        "processing" -> baseColor.copy(alpha = 1.0f)
                        "error" -> Color.Red
                        else -> baseColor.copy(alpha = 0.8f)
                    }

                drawCircle(
                    color = statusColor,
                    center = nodeCenter,
                    radius = 24.dp.toPx()
                )

                // Draw connecting lines
                if (index > 0) {
                    val prevAngle = ((index - 1) * angleStep + rotationAngle) % 360f
                    val prevX = center.x + radius * cos((prevAngle * PI / 180f).toFloat())
                    val prevY = center.y + radius * sin((prevAngle * PI / 180f).toFloat())

                    drawLine(
                        color = NeonTeal.copy(alpha = 0.5f),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Draw task delegation line if dragging
                if (draggingAgent == currentAgentType) { // Compare AgentType with AgentType
                    drawLine(
                        color = NeonTeal,
                        start = nodeCenter,
                        end = nodeCenter + dragOffset,
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
        }

        // Status indicators using BoxWithConstraints to access size in composable context
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxWidth = constraints.maxWidth.toFloat()
            val boxHeight = constraints.maxHeight.toFloat()
            val density = LocalDensity.current.density

            agents.forEachIndexed { index, config -> // config is AgentConfig
                val angle = (index * 360f / agents.size + rotationAngle) % 360f
                val radius = (boxWidth / 2f - 64f)
                val centerX = boxWidth / 2f
                val centerY = boxHeight / 2f
                val x = centerX + radius * cos((angle * PI / 180f).toFloat())
                val y = centerY + radius * sin((angle * PI / 180f).toFloat())

                val textOffsetX = (x - centerX) / density
                val textOffsetY = (y - centerY) / density
                val currentAgentType = try {
                    AgentType.valueOf(config.name.uppercase(Locale.ROOT))
                } catch (e: IllegalArgumentException) {
                    null
                }


                if (currentAgentType != null && agentStatus[currentAgentType] != null) {
                    val statusText = agentStatus[currentAgentType] ?: "idle"

                    Text(
                        text = statusText,
                        color = when (statusText.lowercase(Locale.ROOT)) {
                            "idle" -> NeonTeal
                            "processing" -> NeonPurple
                            "error" -> Color.Red
                            else -> NeonBlue
                        },
                        modifier = Modifier
                            .offset(
                                x = (textOffsetX + 30).dp, // Offset slightly to the right of the node
                                y = (textOffsetY - 10).dp  // Offset slightly above the node
                            )
                    )
                }
            }
        }

        // Center node (Genesis)
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (selectedTask.isNotBlank()) {
                            coroutineScope.launch {
                                viewModel.processQuery(selectedTask)
                                _taskHistory.update { current ->
                                    current + "[GENESIS] $selectedTask"
                                }
                                agentStatus[AgentType.GENESIS] = "processing" // Update directly
                                selectedTask = ""
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = NeonTeal.copy(alpha = 0.8f),
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GENESIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Hive Mind",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple
                    )
                }
            }
        }

        // Task input overlay
        if (draggingAgent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Assign Task to ${draggingAgent?.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal
                        )

                        TextField(
                            value = selectedTask,
                            onValueChange = { selectedTask = it },
                            placeholder = { Text("Enter task description...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                                unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }

        // Task history panel
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd) // Align to top end so it doesn't overlap center/bottom elements
                .fillMaxHeight(0.5f) // Adjust height as needed
                .width(200.dp) // Give it a fixed width or use fillMaxWidth with weight in a Row
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ) // Add a background for visibility
                .padding(8.dp)
        ) {
            Text(
                text = "Task History",
                style = MaterialTheme.typography.titleMedium,
                color = NeonTeal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true,
                state = lazyListState
            ) {
                items(taskHistory.value) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = NeonTeal.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = task,
                            modifier = Modifier.padding(8.dp),
                            color = NeonPurple
                        )
                    }
                }
            }
        }

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { isRotating = !isRotating }
            ) {
                Icon(
                    if (isRotating) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow,
                    contentDescription = "Toggle rotation",
                    tint = NeonPurple
                )
            }

            IconButton(
                onClick = { rotationAngle = 0f }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset rotation",
                    tint = NeonBlue
                )
            }

            IconButton(
                onClick = {
                    _taskHistory.update { emptyList() }
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear history",
                    tint = NeonPink
                )
            }
        }
    }

    // Animation effect
    LaunchedEffect(isRotating) {
        if (isRotating) {
            while (true) {
                rotationAngle = (rotationAngle + 1f) % 360f
                delay(16) // 60 FPS
            }
        }
    }

    // Drag and drop gesture handling
    LaunchedEffect(Unit) {
        snapshotFlow { draggingAgent }
            .collect { agent ->
                if (agent == null) {
                    dragOffset = Offset.Zero
                    dragStartOffset = Offset.Zero
                    selectedTask = ""
                }
            }
    }

    // Task processing status updates
    LaunchedEffect(taskHistory.value) { // Trigger when taskHistory.value changes
        // Reset all agent statuses to idle, then update based on current tasks
        agents.forEach { agentConfig -> // agentConfig is AgentConfig
            try {
                val type = AgentType.valueOf(agentConfig.name.uppercase(Locale.ROOT))
                agentStatus[type] = "idle"
            } catch (e: IllegalArgumentException) {
                // Handle cases where AgentConfig.name might not match an AgentType
            }
        }

        taskHistory.value.forEach { task ->
            val agentNameFromHistory = task.substringAfter("[").substringBefore("]")
            // Compare by name string to find the AgentConfig
            val foundAgentConfig =
                agents.find {
                    it.name.lowercase(Locale.ROOT) == agentNameFromHistory.lowercase(
                        Locale.ROOT
                    )
                }
            if (foundAgentConfig != null) {
                try {
                    val actualAgentType =
                        AgentType.valueOf(foundAgentConfig.name.uppercase(Locale.ROOT))
                    agentStatus[actualAgentType] = "processing"
                    // Simulate task completion after a delay
                    coroutineScope.launch {
                        delay(5000) // Simulate processing time
                        agentStatus[actualAgentType] = "idle"
                    }
                } catch (e: IllegalArgumentException) {
                    // Handle cases where AgentConfig.name might not match an AgentType
                }
            }
        }
    }
}