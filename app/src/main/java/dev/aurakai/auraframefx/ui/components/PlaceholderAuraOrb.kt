package dev.aurakai.auraframefx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal

@Composable
fun FragmentComposable(
    modifier: Modifier = Modifier,
    name: String = "Fragment",
) { // Renamed from Fragment to avoid conflict with Android Fragment class
    // TODO: Implement actual Fragment-like Composable content
    Box(modifier = modifier.padding(8.dp)) {
        Text(text = "$name Placeholder")
    }
}

@Composable
fun PlaceholderAuraOrb(modifier: Modifier = Modifier) { // Renamed
    // TODO: Implement actual Aura Orb Composable with animations and states
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(NeonPurple.copy(alpha = 0.8f))
            .shadow(
                elevation = 24.dp,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Orb",
            color = NeonTeal,
            modifier = Modifier.shadow(
                elevation = 12.dp,
                shape = CircleShape
            )
        )
    }
}

@Preview(showBackground = true, name = "Placeholder Aura Orb Preview")
@Composable
fun PlaceholderAuraOrbPreview() { // Renamed
    PlaceholderAuraOrb()
}

@Preview(showBackground = true, name = "Fragment Composable Preview")
@Composable
fun FragmentComposablePreview() { // Renamed from FragmentPreview
    FragmentComposable(name = "Sample Fragment")
}
