package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * LSPosed Gate Screen - Xposed Module Manager & Quick Access
 *
 * Provides comprehensive Xposed/LSPosed integration including:
 * - Module status monitoring
 * - Hook management and logs
 * - Quick actions for common tasks
 * - Module configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LSPosedGateScreen(
    onNavigateBack: () -> Unit = {}
) {
    var isLSPosedActive by remember { mutableStateOf(true) } // Simulate LSPosed detection
    var activeModules by remember { mutableIntStateOf(3) }
    var totalHooks by remember { mutableIntStateOf(147) }

    // Simulate dynamic updates
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            totalHooks += (kotlin.random.Random.nextInt(-5, 10))
            totalHooks = totalHooks.coerceIn(100, 300)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("LSPosed Control", fontWeight = FontWeight.Bold)
                        Text(
                            "Xposed Framework Manager",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0E27)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0E27))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LSPosed Status Card
            item {
                StatusCard(
                    title = "Framework Status",
                    isActive = isLSPosedActive,
                    activeText = "LSPosed Active",
                    inactiveText = "LSPosed Inactive",
                    stats = listOf(
                        Stat("Active Modules", "$activeModules"),
                        Stat("Total Hooks", "$totalHooks"),
                        Stat("Framework", "v1.9.2")
                    )
                )
            }

            // Quick Actions
            item {
                Text(
                    "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(getQuickActions()) { action ->
                QuickActionCard(action)
            }

            // Module Management
            item {
                Text(
                    "Active Modules",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(getActiveModules()) { module ->
                ModuleCard(module)
            }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    isActive: Boolean,
    activeText: String,
    inactiveText: String,
    stats: List<Stat>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF1B5E20) else Color(0xFF8B0000)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    if (isActive) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (isActive) activeText else inactiveText,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.forEach { stat ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stat.value,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            stat.label,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = action.color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    action.icon,
                    contentDescription = null,
                    tint = Color.White
                )
                Column {
                    Text(
                        action.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        action.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ModuleCard(module: XposedModule) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A)
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        module.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        module.packageName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Badge(
                    containerColor = if (module.enabled) Color.Green else Color.Gray
                ) {
                    Text(
                        if (module.enabled) "ACTIVE" else "DISABLED",
                        fontSize = 10.sp
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                ModuleDetailRow("Version", module.version)
                ModuleDetailRow("Hooks", "${module.hookCount}")
                ModuleDetailRow("Scope", module.scope)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Toggle module */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (module.enabled) Color(0xFF8B0000) else Color(0xFF1B5E20)
                        )
                    ) {
                        Text(if (module.enabled) "Disable" else "Enable")
                    }

                    OutlinedButton(
                        onClick = { /* View logs */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Logs")
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

// Data Classes
private data class Stat(val label: String, val value: String)

private data class QuickAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

private data class XposedModule(
    val name: String,
    val packageName: String,
    val version: String,
    val enabled: Boolean,
    val hookCount: Int,
    val scope: String
)

// Mock Data
private fun getQuickActions() = listOf(
    QuickAction(
        "View Hook Logs",
        "Real-time monitoring of all active hooks",
        Icons.Default.Description,
        Color(0xFF1976D2)
    ),
    QuickAction(
        "Manage Scope",
        "Configure which apps are hooked",
        Icons.Default.Apps,
        Color(0xFF7B1FA2)
    ),
    QuickAction(
        "Reboot System",
        "Soft reboot to apply changes",
        Icons.Default.Refresh,
        Color(0xFFD32F2F)
    ),
    QuickAction(
        "Module Repository",
        "Browse and install Xposed modules",
        Icons.Default.Store,
        Color(0xFF388E3C)
    )
)

private fun getActiveModules() = listOf(
    XposedModule(
        name = "Genesis Protocol",
        packageName = "dev.aurakai.auraframefx",
        version = "0.1.0",
        enabled = true,
        hookCount = 147,
        scope = "SystemUI, Settings, Launcher"
    ),
    XposedModule(
        name = "GravityBox",
        packageName = "com.ceco.gravitybox",
        version = "13.0.0",
        enabled = true,
        hookCount = 89,
        scope = "System Framework"
    ),
    XposedModule(
        name = "MinMinGuard",
        packageName = "tw.fatminmin.xposed.minminguard",
        version = "7.0.1",
        enabled = false,
        hookCount = 0,
        scope = "All Apps"
    )
)
