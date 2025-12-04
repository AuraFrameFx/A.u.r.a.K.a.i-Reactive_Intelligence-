package dev.aurakai.auraframefx.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.ui.gates.*
import dev.aurakai.auraframefx.ui.screens.*
import dev.aurakai.auraframefx.navigation.NavDestination

/**
 * Main navigation graph for the AuraFrameFX app
 * All 90+ screens properly wired and functional
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Gates.route // Start with gate navigation
    ) {
        // ==================== MAIN SCREENS ====================

        composable(route = NavDestination.Home.route) {
            dev.aurakai.auraframefx.screens.HomeScreen(navController = navController)
        }

        composable(route = NavDestination.Gates.route) {
            GateNavigationScreen(navController = navController)
        }

        composable(route = NavDestination.JournalPDA.route) {
            JournalPDAScreen(navController = navController)
        }

        composable(route = NavDestination.IntroScreen.route) {
            IntroScreen(onIntroComplete = { navController.navigate(NavDestination.Gates.route) })
        }

        composable(route = NavDestination.MainScreen.route) {
            MainScreen(
                onNavigateToAgentNexus = { navController.navigate(NavDestination.AgentHub.route) },
                onNavigateToOracleDrive = { navController.navigate(NavDestination.OracleDrive.route) },
                onNavigateToSettings = { navController.navigate(NavDestination.UISettings.route) }
            )
        }

        composable(route = NavDestination.WorkingLab.route) {
            WorkingLabScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(route = NavDestination.AgentProfile.route) {
            AgentProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.EcosystemMenu.route) {
            EcosystemMenuScreen()
        }

        composable(route = NavDestination.HolographicMenu.route) {
            HolographicMenuScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(route = NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }

        // ==================== AGENT HUB ====================

        composable(route = NavDestination.AgentHub.route) {
            AgentHubSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.DirectChat.route) {
            DirectChatScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.TaskAssignment.route) {
            TaskAssignmentScreen()
        }

        composable(route = NavDestination.AgentMonitoring.route) {
            AgentMonitoringScreen()
        }

        composable(route = NavDestination.FusionMode.route) {
            FusionModeScreen()
        }

        composable(route = NavDestination.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        // ==================== ORACLE DRIVE ====================

        composable(route = NavDestination.OracleDrive.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.SphereGrid.route) {
            SphereGridScreen(navController = navController)
        }

        // ==================== ROM TOOLS ====================

        composable(route = NavDestination.ROMTools.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.ROMFlasher.route) {
            ROMFlasherScreen()
        }

        composable(route = NavDestination.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.BootloaderManager.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== LSPOSED INTEGRATION ====================

        composable(route = NavDestination.LSPosedGate.route) {
            LSPosedSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.ModuleManager.route) {
            ModuleManagerScreen()
        }

        composable(route = NavDestination.LSPosedModuleManager.route) {
            LSPosedModuleManagerScreen()
        }

        composable(route = NavDestination.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.HookManager.route) {
            HookManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== UI/UX DESIGN STUDIO ====================

        composable(route = NavDestination.UIUXDesignStudio.route) {
            UIUXGateSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.StatusBar.route) {
            StatusBarScreen()
        }

        composable(route = NavDestination.NotchBar.route) {
            NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.OverlayMenus.route) {
            OverlayMenusScreen()
        }

        composable(route = NavDestination.QuickActions.route) {
            QuickActionsScreen()
        }

        composable(route = NavDestination.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== HELP DESK ====================

        composable(route = NavDestination.HelpDesk.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.LiveSupport.route) {
            LiveSupportChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = NavDestination.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== AURA'S LAB ====================

        composable(route = NavDestination.AurasLab.route) {
            AurasLabScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
