package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
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
import androidx.navigation.NavHostController

/**
 * Help Desk Gate Submenu
 * User support and documentation center
 */
@Composable
fun HelpDeskSubmenuScreen(
    navController: NavHostController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "FAQ Browser",
            description = "Frequently asked questions and quick answers",
            icon = Icons.AutoMirrored.Filled.Help,
            route = "faq_browser",
            color = Color(0xFF4169E1) // Royal Blue
        ),
        SubmenuItem(
            title = "Live Support Chat",
            description = "Real-time assistance from support agents",
            icon = Icons.Default.Chat,
            route = "live_support_chat",
            color = Color(0xFF32CD32) // Lime Green
        ),
        SubmenuItem(
            title = "Tutorial Videos",
            description = "Step-by-step guides and feature walkthroughs",
            icon = Icons.Default.PlayArrow,
            route = "tutorial_videos",
            color = Color(0xFFFFD700) // Gold
        ),
        SubmenuItem(
            title = "Documentation",
            description = "Comprehensive user guides and API reference",
            icon = Icons.Default.MenuBook,
            route = "documentation",
            color = Color(0xFF9370DB) // Medium Purple
        )
    )

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
                text = "❓ HELP DESK",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF4169E1),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "User support and documentation center",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6495ED).copy(alpha = 0.8f) // Cornflower Blue
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Support Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4169E1))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Support Agents Online
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agents Online",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Avg Response Time
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "2m",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avg Response",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Tickets Resolved
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "247",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF9370DB),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Resolved Today",
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
                            containerColor = Color(0xFF4169E1).copy(alpha = 0.2f)
                        )
                    ) {
                        Text("← Back to Gates", color = Color(0xFF4169E1))
                    }
                }
            }
        }
    }
}
