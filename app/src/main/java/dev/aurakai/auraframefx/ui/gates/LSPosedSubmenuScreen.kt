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

            // Quick Actions Panel
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            XposedQuickActionsPanel()

            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                text = "Advanced Options",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

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

/**
 * Xposed Quick Actions Panel
 * Provides instant access to common Xposed framework operations
 */
@Composable
private fun XposedQuickActionsPanel() {
    var modulesEnabled by remember { mutableStateOf(true) }
    var showHooksDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status message
            if (statusMessage != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = statusMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Cyan
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Quick action buttons in 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enable/Disable Modules
                QuickActionButton(
                    title = if (modulesEnabled) "Disable Modules" else "Enable Modules",
                    icon = Icons.Default.Extension,
                    color = if (modulesEnabled) Color(0xFFFF6B35) else Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            modulesEnabled = !modulesEnabled
                            statusMessage = if (modulesEnabled) {
                                "Modules enabled. Reboot required."
                            } else {
                                "Modules disabled. Reboot required."
                            }
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )

                // View Active Hooks
                QuickActionButton(
                    title = "View Hooks",
                    icon = Icons.Default.CallSplit,
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showHooksDialog = true
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Framework Restart
                QuickActionButton(
                    title = "Restart Framework",
                    icon = Icons.Default.Refresh,
                    color = Color(0xFFFFD93D),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            statusMessage = "Restarting Xposed framework..."
                            // TODO: Implement framework restart
                            kotlinx.coroutines.delay(2000)
                            statusMessage = "Framework restarted successfully"
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )

                // Clear Hook Cache
                QuickActionButton(
                    title = "Clear Cache",
                    icon = Icons.Default.DeleteOutline,
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            statusMessage = "Clearing hook cache..."
                            // TODO: Implement cache clear
                            kotlinx.coroutines.delay(1500)
                            statusMessage = "Hook cache cleared"
                            kotlinx.coroutines.delay(3000)
                            statusMessage = null
                        }
                    }
                )
            }
        }
    }

    // Active Hooks Dialog
    if (showHooksDialog) {
        AlertDialog(
            onDismissRequest = { showHooksDialog = false },
            title = {
                Text(
                    "Active Hooks",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Currently active hooks in the system:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HookItem("SystemUI", "247 hooks", Color(0xFF4ECDC4))
                    HookItem("Settings", "84 hooks", Color(0xFFFFD93D))
                    HookItem("Package Manager", "56 hooks", Color(0xFF32CD32))
                    HookItem("Activity Manager", "102 hooks", Color(0xFFFF6B35))
                    Text(
                        "Total: 489 active hooks",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHooksDialog = false }) {
                    Text("Close", color = Color.Cyan)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun HookItem(
    packageName: String,
    hookCount: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Text(
                text = packageName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White
                )
            )
        }
        Text(
            text = hookCount,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

