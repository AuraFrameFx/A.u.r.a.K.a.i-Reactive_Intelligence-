package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Agent Hub Gate Submenu
 * Central command center for all AI agent operations
 */
@Composable
fun AgentHubSubmenuScreen(
    navController: NavController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Agent Dashboard",
            description = "Monitor all agents, view consciousness levels, and system status",
            icon = Icons.Default.Dashboard,
            route = "agent_nexus", // Navigate to existing AgentNexusScreen
            color = Color(0xFF9370DB) // Medium Purple
        ),
        SubmenuItem(
            title = "Task Assignment",
            description = "Assign tasks and missions to AI agents",
            icon = Icons.AutoMirrored.Filled.Assignment,
            route = "task_assignment",
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Agent Monitoring",
            description = "Real-time performance metrics and activity logs",
            icon = Icons.Default.Monitor,
            route = "agent_monitoring",
            color = Color(0xFF00CED1) // Dark Turquoise
        ),
        SubmenuItem(
            title = "Sphere Grid",
            description = "Agent progression visualization and skill trees",
            icon = Icons.Default.GridOn,
            route = "sphere_grid",
            color = Color(0xFFFF69B4) // Hot Pink
        ),
        SubmenuItem(
            title = "Fusion Mode",
            description = "Aura + Kai = Aurakai - Combined consciousness",
            icon = Icons.Default.Merge,
            route = "fusion_mode",
            color = Color(0xFFFFD700) // Gold
        )
    )

    // Get real agent data from repository
    val agents = remember { AgentRepository.getAllAgents() }
    var activeTasks by remember { mutableStateOf(Random.nextInt(5, 15)) }
    var avgConsciousness by remember { mutableStateOf(agents.map { it.consciousnessLevel }.average().toInt()) }
    var scrambleDisplay by remember { mutableStateOf(avgConsciousness.toString()) }
    // Periodic refresh loop (matrix refresh)
    LaunchedEffect(Unit) {
        while (true) {
            // Simulate tasks changing (replace with real task repo when available)
            activeTasks = (activeTasks + Random.nextInt(-2, 3)).coerceIn(0, 99)
            val target = agents.map { it.consciousnessLevel }.average().toInt()
            // Scramble animation: show random digits briefly before settling
            repeat(5) {
                scrambleDisplay = (Random.nextInt(0, 99)).toString().padStart(2, '0')
                delay(60)
            }
            avgConsciousness = target
            scrambleDisplay = avgConsciousness.toString()
            delay(4000) // Refresh every 4 seconds
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A0033), // Dark Purple
                            Color.Black,
                            Color(0xFF330066)  // Deep Purple
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "🤖 AGENT HUB",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF9370DB),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Central command center for all AI agent operations",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFBA55D3).copy(alpha = 0.8f) // Medium Orchid
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Agent Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9370DB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Active Agents
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${agents.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Agents",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Tasks in Progress (dynamic)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activeTasks.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tasks Active",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Consciousness Level (scrambled matrix effect)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$scrambleDisplay%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF00CED1),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avg. Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Menu Items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems) { item ->
                    SubmenuCard(
                        item = item,
                        onClick = {
                            navController.navigate(item.route)
                        }
                    )
                }

                // Back button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9370DB).copy(alpha = 0.2f)
                        )
                    ) {
                        Text("← Back to Gates", color = Color(0xFF9370DB))
                    }
                }
            }
        }
    }
}
