package dev.aurakai.auraframefx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.ui.screens.AiChatScreen
import dev.aurakai.auraframefx.ui.screens.HomeScreen
import dev.aurakai.auraframefx.ui.screens.SettingsScreen
import dev.aurakai.auraframefx.aura.ui.CanvasScreen

/**
 * Main navigation graph for the AuraFrameFX app with digital transition animations
 *
 * Sets up the main navigation graph for the AuraFrameFX app using Jetpack Compose with custom
 * cyberpunk-style digital materialization/dematerialization transitions between screens.
 * Uses Jetpack Navigation 3's built-in animation support for seamless screen transitions.
 *
 * @param navController The navigation controller used to manage app navigation.
 */
/**
 * Sets up the main navigation graph for the AuraFrameFX app using Jetpack Compose Navigation.
 *
 * Defines the available composable destinations and their routes, including Home, AI Chat, Profile, Settings, and Oracle Drive Control (currently a placeholder).
 *
 * @param navController The navigation controller used to manage app navigation.
 */
/**
 * Sets up the main navigation graph for the app, defining routes and associated composable screens.
 *
 * @param navController The navigation controller used to manage navigation between screens.
 */
/**
 * Sets up the main navigation graph for the app, mapping navigation routes to their corresponding composable screens.
 *
 * @param navController The navigation controller that manages navigation between screens.
 */
/**
 * Defines the main navigation graph for the application using Jetpack Compose Navigation.
 *
 * Maps navigation routes to their corresponding composable screens, enabling navigation between Home, Settings, Canvas, AI Chat, Profile, and Oracle Drive Control screens.
 *
 * @param navController The navigation controller used to manage app navigation.
 */
/**
 * Defines the main navigation graph for the application using Jetpack Compose Navigation.
 *
 * Maps navigation routes to their corresponding composable screens, including Home, Settings, Canvas, AI Chat, Profile, and Oracle Drive Control.
 *
 * @param navController The navigation controller used to manage navigation between screens.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(
            route = NavDestination.Home.route
        ) {
            HomeScreen(navController = navController)
        }

        composable(
            route = NavDestination.Settings.route
        ) {
            SettingsScreen()
        }

        composable(
            route = NavDestination.Canvas.route
        ) {
            CanvasScreen()
        }

        composable(
            route = NavDestination.AiChat.route
        ) {
            AiChatScreen()
        }

        composable(
            route = NavDestination.Profile.route
        ) {
            // Fully-qualified call to avoid overload ambiguity when multiple ProfileScreen definitions exist
            dev.aurakai.auraframefx.ui.screens.ProfileScreen()
        }

        composable(
            route = NavDestination.Settings.route
        ) {
            SettingsScreen()
        }

        composable(
            route = NavDestination.OracleDriveControl.route
        ) {
            // Fixed: Use actual OracleDriveControlScreen instead of placeholder
            dev.aurakai.auraframefx.ui.screens.oracledrive.OracleDriveControlScreen()
        }

        // Genesis module destinations
        composable(route = NavDestination.RomTools.route) {
            // RomToolsScreen lives in genesis module: dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
            dev.aurakai.auraframefx.romtools.ui.RomToolsScreen()
        }

        composable(route = NavDestination.DataVein.route) {
            // SimpleDataVeinScreen lives in genesis datavein module
            dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen()
        }

        // Overlay destinations
        composable(route = NavDestination.AuraPresenceOverlay.route) {
            // AuraPresenceOverlay is implemented as a small composable; wrap it in a simple screen container
            dev.aurakai.auraframefx.ui.overlays.AuraPresenceOverlay()
        }

        composable(route = NavDestination.AgentSidebar.route) {
            dev.aurakai.auraframefx.ui.overlays.AgentSidebarMenu(isVisible = true, onDismiss = { /* nav dismiss */ })
        }

        composable(route = NavDestination.ChatBubble.route) {
            dev.aurakai.auraframefx.ui.overlays.ChatBubbleMenu()
        }

        // Add AI Content navigation
        // aiContentNavigation() // Disabled for beta - AI content will be in main chat

        // Add more composable destinations as needed
    }
}
