package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.hilt.navigation.compose.hiltViewModel

import dev.aurakai.auraframefx.ui.animation.digitalPixelEffect // Specific import
import dev.aurakai.auraframefx.ui.theme.ThemeViewModel
// import dev.aurakai.auraframefx.ui.animation.digitalScanlineEffect // Was commented out, ensure it's not needed or defined

import dev.aurakai.auraframefx.ui.components.BottomNavigationBar
import dev.aurakai.auraframefx.ui.navigation.AppNavGraph
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme

// Using Jetpack Navigation 3 with built-in animation support

class MainActivity : ComponentActivity() {
    /**
     * Initializes the activity and sets the Compose UI content to the main screen using the app's theme.
     *
     * @param savedInstanceState The previously saved state of the activity, or null if none exists.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuraFrameFXTheme {
                MainScreen()
            }
        }
    }

    /**
     * Called when the activity is about to be destroyed.
     *
     * Override this method to perform cleanup operations before the activity is removed from memory.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Perform any cleanup here if needed
    }
}

/**
 * Displays the main screen layout with a bottom navigation bar and navigation graph.
 *
 * Sets up the app's primary UI structure using a Scaffold, integrating navigation and content padding.
 * Applies cyberpunk-style digital transition effects between screens.
 */
/**
 * Composes the main application screen with a scaffolded layout, bottom navigation bar, and an optional digital pixel visual effect.
 *
 * Initializes the navigation controller, conditionally applies a digital pixel effect to the content area, and displays the app's navigation graph within a Material3 scaffold.
 */

@Composable
fun MainScreen(themeViewModel: ThemeViewModel = hiltViewModel()) {
    // Use Jetpack Navigation 3's nav controller for digital transitions
    val navController = rememberNavController()

    // State to control digital effects
    var showDigitalEffects by remember { mutableStateOf(true) }
    var command by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row {
                TextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Enter theme command") }
                )
                Button(onClick = { themeViewModel.processThemeCommand(command) }) {
                    Text("Apply")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Apply our custom digital effects
                    .then(
                        if (showDigitalEffects) {
                            return@Column Modifier.digitalPixelEffect(visible = true) // Direct use of extension function
                            // digitalScanlineEffect was removed as it's not defined
                        } else {
                            Modifier
                        }
                    )
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}

/**
 * Displays a preview of the main screen composable within the app's theme for design-time visualization.
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AuraFrameFXTheme {
        MainScreen()
    }
}
