package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Small back affordance for testing/navigation
        Text(
            text = "← Back",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clickable { onNavigateBack() }
        )
        Text(
            text = "Canvas (placeholder)",
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
