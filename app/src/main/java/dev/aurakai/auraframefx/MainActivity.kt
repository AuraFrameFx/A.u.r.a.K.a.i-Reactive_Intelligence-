package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.hilt.navigation.compose.hiltViewModel

import dev.aurakai.auraframefx.ui.animation.digitalPixelEffect // Specific import
import dev.aurakai.auraframefx.ui.theme.ThemeViewModel
// import dev.aurakai.auraframefx.ui.animation.digitalScanlineEffect // Was commented out, ensure it's not needed or defined

import dev.aurakai.auraframefx.ui.components.BottomNavigationBar
import dev.aurakai.auraframefx.navigation.AppNavGraph
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
                val themeViewModel: ThemeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                MainScreen(themeViewModel = themeViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Perform any cleanup here if needed
    }
}

// New: a preview-friendly content composable that accepts a lambda for theme commands
@Composable
internal fun MainScreenContent(
    processThemeCommand: (String) -> Unit
) {
    val navController = rememberNavController()

    var showDigitalEffects by remember { mutableStateOf(true) }
    var command by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row {
                TextField(
                    value = command,
                    onValueChange = { newValue -> command = newValue },
                    label = { Text("Enter theme command") }
                )
                Button(onClick = { processThemeCommand(command) }) {
                    Text("Apply")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Apply our custom digital effects
                    .then(
                        if (showDigitalEffects) {
                            return@Column Modifier.digitalPixelEffects(visible = true) // Direct use of extension function
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

fun Modifier.Companion.digitalPixelEffects(visible: Boolean) {}

// Keep original API used by Activity: delegate to the content with the real ViewModel
@Composable
internal fun MainScreen(
    themeViewModel: ThemeViewModel
) {
    MainScreenContent(processThemeCommand = { themeViewModel.processThemeCommand(it) })
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AuraFrameFXTheme {
        // For preview, use a no-op lambda for the theme command handler
        MainScreenContent(processThemeCommand = { /* no-op in preview */ })
    }
}
