package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * LSPosed Gate Submenu
 * Xposed framework management and module control
 */
@Composable
fun LSPosedSubmenuScreen(
    navController: NavController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Module Manager",
            description = "Install, enable, and configure Xposed modules",
            icon = Icons.Default.Extension,
            route = "module_manager_lsposed",
            color = Color(0xFFFF6B35) // Orange Red
        ),
        SubmenuItem(
            title = "Hook Manager",
            description = "Monitor and manage active method hooks",
            icon = Icons.Default.CallSplit,
            route = "hook_manager",
            color = Color(0xFF4ECDC4) // Teal
        ),
        SubmenuItem(
            title = "Logs Viewer",
            description = "View system logs and module activity",
            icon = Icons.Default.ListAlt,
            route = "logs_viewer",
            color = Color(0xFFFFD93D) // Yellow
        ),
        SubmenuItem(
            title = "Quick Actions",
            description = "Common operations and shortcuts",
            icon = Icons.Default.Bolt,
            route = "quick_actions",
            color = Color(0xFF6C5CE7) // Purple
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
                text = "🔧 LSPOSED GATE",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Xposed framework management and module control",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFFF8C69).copy(alpha = 0.8f) // Coral
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Framework Status Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B35))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Active Modules
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "12",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF4ECDC4),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Modules",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Framework Status
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Framework",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // System Hooks
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "247",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD93D),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active Hooks",
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
                            containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
                        )
                    ) {
                        Text("← Back to Gates", color = Color(0xFFFF6B35))
                    }
                }
            }
        }
    }
}

