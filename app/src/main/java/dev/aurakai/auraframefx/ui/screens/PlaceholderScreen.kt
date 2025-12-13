package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlaceholderScreen() { // Renamed to placeholderScreen
    // TODO: Implement the actual Placeholder Screen UI or replace with specific screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Placeholder Screen")
    }
}

// @Preview(showBackground = true)
// @Composable
// fun PlaceholderScreenPreview() { // Renamed
//     PlaceholderScreen()
// }
