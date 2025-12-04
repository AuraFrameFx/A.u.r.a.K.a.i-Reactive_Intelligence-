package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
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
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

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
