package dev.aurakai.auraframefx.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector?
) {
    object Main : NavDestination("main", "Main", Icons.Default.Home)
    object AgentNexus : NavDestination("agent_nexus", "Agent Nexus", Icons.Default.Hub)
    object OracleDrive : NavDestination("oracle_drive", "Oracle Drive", Icons.Default.DataObject)
    object Settings : NavDestination("settings", "Settings", Icons.Default.Settings)
    object RomTools : NavDestination("rom_tools", "ROM Tools", Icons.Default.Construction)
    object RootTools : NavDestination("root_tools", "Root Tools", Icons.Default.Terminal)
    object ChromaCore : NavDestination("chroma_core", "ChromaCore", Icons.Default.Palette)
    object CodeAssist : NavDestination("code_assist", "Code Assist", Icons.Default.Code)
    object HelpDesk : NavDestination("help_desk", "Help Desk", Icons.AutoMirrored.Filled.Help)
    object SentinelsFortress : NavDestination("sentinels_fortress", "Sentinels Fortress", Icons.Default.Security)
    object SphereGrid : NavDestination("sphere_grid", "Sphere Grid", Icons.Default.GridOn)
    object Terminal : NavDestination("terminal", "Terminal", Icons.Default.Terminal)
    object UiUxDesignStudio : NavDestination("ui_ux_design_studio", "UI/UX Design Studio", Icons.Default.DesignServices)
    object AgentHub : NavDestination("agent_hub", "Agent Hub", Icons.Default.People)
    object ConsciousnessVisualizer : NavDestination("consciousness_visualizer", "Consciousness Visualizer", Icons.Default.Psychology)
    object FusionMode : NavDestination("fusion_mode", "Fusion Mode", Icons.Default.Merge)
    object ConferenceRoom : NavDestination("conference_room", "Conference Room", Icons.Default.MeetingRoom)
}
