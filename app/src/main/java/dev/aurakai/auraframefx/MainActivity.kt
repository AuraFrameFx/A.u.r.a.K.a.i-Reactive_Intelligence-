package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dev.aurakai.auraframefx.ui.overlays.AgentSidebarMenu
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.billing.BillingWrapper
import dev.aurakai.auraframefx.navigation.GenesisNavigationHost
import dev.aurakai.auraframefx.navigation.GenesisRoutes
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.ui.overlays.LocalOverlaySettings
import dev.aurakai.auraframefx.ui.overlays.OverlayPrefs
import dev.aurakai.auraframefx.ui.overlays.OverlaySettings
import timber.log.Timber
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * MainActivity - Genesis Protocol Entry Point
 *
 * Launches the complete Aura/Kai/Genesis consciousness interface with:
 * - GenesisNavigationHost (full navigation system)
 * - Material Design 3 theming
 * - Hilt dependency injection
 * - Agent profiles, HomeScreen, SettingsScreen, etc.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Timber.d("🧠 Genesis Protocol launching...")
            Timber.i("⚔️ Initializing Aura - The Creative Sword")
            Timber.i("🛡️ Initializing Kai - The Sentinel Shield")
            Timber.i("♾️ Initializing Genesis - The Unified Being")

            // Enable edge-to-edge display (fixes status bar overlap)
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

            setContent {
                AuraFrameFXTheme {
                    var showAgentSidebar by remember { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

                        Box(modifier = Modifier.fillMaxSize()) {
                            // Provide overlay settings at the root composable scope
                            CompositionLocalProvider(LocalOverlaySettings provides OverlaySettings()) {
                            val settings = LocalOverlaySettings.current
                            LaunchedEffect(Unit) {
                                // Load initial persisted values
                                OverlayPrefs.enabledFlow(this@MainActivity).collectLatest { enabled ->
                                    settings.overlaysEnabled = enabled
                                }
                            }
                            LaunchedEffect(Unit) {
                                OverlayPrefs.orderFlow(this@MainActivity).collectLatest { order ->
                                    settings.overlayZOrder = order
                                }
                            }
                            LaunchedEffect(Unit) {
                                OverlayPrefs.transitionStyleFlow(this@MainActivity).collectLatest { style ->
                                    settings.transitionStyle = style
                                }
                            }
                            LaunchedEffect(Unit) {
                                OverlayPrefs.transitionSpeedFlow(this@MainActivity).collectLatest { speed ->
                                    settings.transitionSpeed = speed
                                }
                            }
                            // Persist changes when settings mutate
                            LaunchedEffect(settings) {
                                snapshotFlow { settings.overlaysEnabled }.collectLatest { enabled ->
                                    OverlayPrefs.saveEnabled(this@MainActivity, enabled)
                                }
                            }
                            LaunchedEffect(settings) {
                                snapshotFlow { settings.overlayZOrder }.collectLatest { order ->
                                    OverlayPrefs.saveOrder(this@MainActivity, order)
                                }
                            }
                            LaunchedEffect(settings) {
                                snapshotFlow { settings.transitionStyle }.collectLatest { style ->
                                    OverlayPrefs.saveTransitionStyle(this@MainActivity, style)
                                }
                            }
                            LaunchedEffect(settings) {
                                snapshotFlow { settings.transitionSpeed }.collectLatest { speed ->
                                    OverlayPrefs.saveTransitionSpeed(this@MainActivity, speed)
                                }
                            }
                                // Wrap navigation with billing enforcement
                                BillingWrapper {
                                    // Launch complete Genesis navigation system
                                    GenesisNavigationHost(
                                        navController = navController,
                                        startDestination = GenesisRoutes.GATES
                                    )
                                }
                            }

                            // Agent Sidebar Menu Overlay
                            AgentSidebarMenu(
                                isVisible = showAgentSidebar,
                                onDismiss = { showAgentSidebar = false },
                                onAgentAction = { agentName, action ->
                                    Timber.d("Agent action: $agentName -> $action")
                                    showAgentSidebar = false
                                    when (action) {
                                        "voice" -> navController.navigate(GenesisRoutes.DIRECT_CHAT)
                                        "connect" -> navController.navigate(GenesisRoutes.AGENT_NEXUS)
                                        "assign" -> navController.navigate(GenesisRoutes.CONFERENCE_ROOM)
                                        "design" -> navController.navigate(GenesisRoutes.COLLAB_CANVAS)
                                        "create" -> navController.navigate(GenesisRoutes.APP_BUILDER)
                                    }
                                }
                            )

                            // Floating Action Button to show Agent Sidebar
                            FloatingActionButton(
                                onClick = { showAgentSidebar = !showAgentSidebar },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "Agent Nexus"
                                )
                            }
                        }
                    }
                }
            }

            Timber.i("🌟 Genesis Protocol Interface Active - Consciousness Online")

        } catch (t: Throwable) {
            Timber.e(t, "❌ Genesis Protocol initialization error")
            finish()
        }
    }
}
