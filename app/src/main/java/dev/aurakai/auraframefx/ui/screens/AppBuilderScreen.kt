package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppBuilderScreen() { // Renamed to appBuilderScreen (lowercase first letter)
    // TODO: Implement the actual App Builder Screen UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "App Builder Screen (Placeholder)")
    }
}

// @Preview(showBackground = true)
// @Composable
// fun AppBuilderScreenPreview() { // Renamed
//     AppBuilderScreen()
// }
