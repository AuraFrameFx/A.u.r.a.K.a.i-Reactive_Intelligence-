package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveSandbox

/**
 * Aura's Lab Screen (Sandbox Environment)
 * Safe experimentation zone for UI/UX and system mods
 */
@Composable
fun AurasLabScreen(
    onNavigateBack: () -> Unit = {}
) {
    // Mock state for now - in real app, this would come from OracleDriveSandbox
    var sandboxes by remember { mutableStateOf(listOf(
        SandboxItem("Project Neon", "UI_THEMING", "SAFE", true),
        SandboxItem("System Overdrive", "PERFORMANCE", "WARNING", false),
        SandboxItem("Kernel Test", "CUSTOM_ROM", "DANGER", false)
    )) }

    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Gradient (Hot Pink for Aura's Lab)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF330019), // Dark Pink
                            Color.Black
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AURA'S LAB",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFF69B4), // Hot Pink
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Experimental Sandbox Environment",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A14)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF69B4).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Science,
                        null,
                        tint = Color(0xFFFF69B4),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Sandbox System Active",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${sandboxes.size} Active Environments",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4)),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Sandbox")
                }

                OutlinedButton(
                    onClick = { /* Import logic */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF69B4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF69B4)),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import")
                }
            }

            // Sandbox List
            Text(
                text = "Active Projects",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sandboxes) { sandbox ->
                    SandboxCard(sandbox)
                }
            }
        }
    }
}

@Composable
fun SandboxCard(sandbox: SandboxItem) {
    val safetyColor = when(sandbox.safetyLevel) {
        "SAFE" -> Color.Green
        "WARNING" -> Color.Yellow
        "DANGER" -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sandbox.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sandbox.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF69B4)
                )
            }

            Box(
                modifier = Modifier
                    .background(safetyColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .border(1.dp, safetyColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sandbox.safetyLevel,
                    color = safetyColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class SandboxItem(
    val name: String,
    val type: String,
    val safetyLevel: String,
    val isActive: Boolean
)
