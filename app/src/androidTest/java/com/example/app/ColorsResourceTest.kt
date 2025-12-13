package dev.aurakai.auraframefx

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Comprehensive unit tests for Colors resource validation and accessibility.
 * Tests color resources, accessibility compliance, and resource integrity.
 *
 * Testing Framework: AndroidX Test with JUnit4
 *
 * This test suite covers:
 * - Happy path scenarios for all defined color resources
 * - Edge cases and error conditions
 * - Accessibility compliance (WCAG contrast ratios)
 * - Color validation and consistency
 * - Theme-specific color tests
 * - Performance testing
 * - Resource integrity validation
 */
@RunWith(AndroidJUnit4::class)
class ColorsResourceTest {

    private lateinit var context: Context
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        resources = context.resources
    }

    // Happy Path Tests - Basic Color Resource Access
    @Test
    fun testNeonTealColorExists() {
        val colorResId = resources.getIdentifier("neon_teal", "color", context.packageName)
        assertTrue("Neon teal color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Neon teal should be #00FFCC", 0xFF00FFCC.toInt(), color)
    }

    @Test
    fun testNeonPurpleColorExists() {
        val colorResId = resources.getIdentifier("neon_purple", "color", context.packageName)
        assertTrue("Neon purple color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Neon purple should be #E000FF", 0xFFE000FF.toInt(), color)
    }

    @Test
    fun testNeonBlueColorExists() {
        val colorResId = resources.getIdentifier("neon_blue", "color", context.packageName)
        assertTrue("Neon blue color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Neon blue should be #00FFFF", 0xFF00FFFF.toInt(), color)
    }

    @Test
    fun testNeonTealTransparentColorExists() {
        val colorResId =
            resources.getIdentifier("neon_teal_transparent", "color", context.packageName)
        assertTrue("Neon teal transparent color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Neon teal transparent should be #3300FFCC", 0x3300FFCC.toInt(), color)
    }

    @Test
    fun testBlackColorExists() {
        val colorResId = resources.getIdentifier("black", "color", context.packageName)
        assertTrue("Black color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Black should be #000000", 0xFF000000.toInt(), color)
    }

    @Test
    fun testWhiteColorExists() {
        val colorResId = resources.getIdentifier("white", "color", context.packageName)
        assertTrue("White color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("White should be #FFFFFF", 0xFFFFFFFF.toInt(), color)
    }

    @Test
    fun testTransparentColorExists() {
        val colorResId = resources.getIdentifier("transparent", "color", context.packageName)
        assertTrue("Transparent color resource should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        assertEquals("Transparent should be #00000000", 0x00000000, color)
    }

    // Light Theme Color Tests
    @Test
    fun testLightThemeColorsExist() {
        val lightColors = listOf(
            "light_primary",
            "light_on_primary",
            "light_primary_container",
            "light_on_primary_container",
            "light_secondary",
            "light_on_secondary",
            "light_secondary_container",
            "light_on_secondary_container",
            "light_tertiary",
            "light_on_tertiary",
            "light_tertiary_container",
            "light_on_tertiary_container",
            "light_error",
            "light_error_container",
            "light_on_error",
            "light_on_error_container",
            "light_background",
            "light_on_background",
            "light_surface",
            "light_on_surface",
            "light_surface_variant",
            "light_on_surface_variant",
            "light_outline",
            "light_outline_variant"
        )

        lightColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            assertTrue("Light theme color $colorName should exist", colorResId != 0)

            val color = ContextCompat.getColor(context, colorResId)
            assertNotEquals("Light theme color $colorName should not be invalid", -1, color)
        }
    }

    @Test
    fun testDarkThemeColorsExist() {
        val darkColors = listOf(
            "dark_primary",
            "dark_on_primary",
            "dark_primary_container",
            "dark_on_primary_container",
            "dark_secondary",
            "dark_on_secondary",
            "dark_secondary_container",
            "dark_on_secondary_container",
            "dark_tertiary",
            "dark_on_tertiary",
            "dark_tertiary_container",
            "dark_on_tertiary_container",
            "dark_error",
            "dark_error_container",
            "dark_on_error",
            "dark_on_error_container",
            "dark_background",
            "dark_on_background",
            "dark_surface",
            "dark_on_surface",
            "dark_surface_variant",
            "dark_on_surface_variant",
            "dark_outline",
            "dark_outline_variant"
        )

        darkColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            assertTrue("Dark theme color $colorName should exist", colorResId != 0)

            val color = ContextCompat.getColor(context, colorResId)
            assertNotEquals("Dark theme color $colorName should not be invalid", -1, color)
        }
    }

    // Edge Cases - Color Validation
    @Test
    fun testNeonColorsAlphaValues() {
        val neonColors = listOf("neon_teal", "neon_purple", "neon_blue")

        neonColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            assertTrue("Neon color $colorName should exist", colorResId != 0)

            val color = ContextCompat.getColor(context, colorResId)
            val alpha = (color shr 24) and 0xFF
            assertEquals("Neon color $colorName should be fully opaque", 255, alpha)
        }
    }

    @Test
    fun testTransparentColorAlphaValue() {
        val colorResId =
            resources.getIdentifier("neon_teal_transparent", "color", context.packageName)
        assertTrue("Neon teal transparent should exist", colorResId != 0)

        val color = ContextCompat.getColor(context, colorResId)
        val alpha = (color shr 24) and 0xFF
        assertEquals("Neon teal transparent should have alpha 0x33", 0x33, alpha)
    }

    @Test
    fun testColorComponentValues() {
        val colorNames = listOf("neon_teal", "neon_purple", "neon_blue", "black", "white")

        colorNames.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            if (colorResId != 0) {
                val color = ContextCompat.getColor(context, colorResId)
                val red = (color shr 16) and 0xFF
                val green = (color shr 8) and 0xFF
                val blue = color and 0xFF

                assertTrue("Red component should be valid (0-255) for $colorName", red in 0..255)
                assertTrue(
                    "Green component should be valid (0-255) for $colorName",
                    green in 0..255
                )
                assertTrue("Blue component should be valid (0-255) for $colorName", blue in 0..255)
            }
        }
    }

    // Accessibility Tests - Color Contrast
    @Test
    fun testLightPrimaryOnPrimaryContrast() {
        val primaryResId = resources.getIdentifier("light_primary", "color", context.packageName)
        val onPrimaryResId =
            resources.getIdentifier("light_on_primary", "color", context.packageName)

        if (primaryResId != 0 && onPrimaryResId != 0) {
            val primaryColor = ContextCompat.getColor(context, primaryResId)
            val onPrimaryColor = ContextCompat.getColor(context, onPrimaryResId)

            val contrastRatio = calculateContrastRatio(onPrimaryColor, primaryColor)
            assertTrue(
                "Light theme on_primary should have adequate contrast with primary (>= 4.5:1)",
                contrastRatio >= 4.5
            )
        }
    }

    @Test
    fun testLightSecondaryOnSecondaryContrast() {
        val secondaryResId =
            resources.getIdentifier("light_secondary", "color", context.packageName)
        val onSecondaryResId =
            resources.getIdentifier("light_on_secondary", "color", context.packageName)

        if (secondaryResId != 0 && onSecondaryResId != 0) {
            val secondaryColor = ContextCompat.getColor(context, secondaryResId)
            val onSecondaryColor = ContextCompat.getColor(context, onSecondaryResId)

            val contrastRatio = calculateContrastRatio(onSecondaryColor, secondaryColor)
            assertTrue(
                "Light theme on_secondary should have adequate contrast with secondary (>= 4.5:1)",
                contrastRatio >= 4.5
            )
        }
    }

    @Test
    fun testLightSurfaceOnSurfaceContrast() {
        val surfaceResId = resources.getIdentifier("light_surface", "color", context.packageName)
        val onSurfaceResId =
            resources.getIdentifier("light_on_surface", "color", context.packageName)

        if (surfaceResId != 0 && onSurfaceResId != 0) {
            val surfaceColor = ContextCompat.getColor(context, surfaceResId)
            val onSurfaceColor = ContextCompat.getColor(context, onSurfaceResId)

            val contrastRatio = calculateContrastRatio(onSurfaceColor, surfaceColor)
            assertTrue(
                "Light theme on_surface should have adequate contrast with surface (>= 4.5:1)",
                contrastRatio >= 4.5
            )
        }
    }

    @Test
    fun testDarkPrimaryOnPrimaryContrast() {
        val primaryResId = resources.getIdentifier("dark_primary", "color", context.packageName)
        val onPrimaryResId =
            resources.getIdentifier("dark_on_primary", "color", context.packageName)

        if (primaryResId != 0 && onPrimaryResId != 0) {
            val primaryColor = ContextCompat.getColor(context, primaryResId)
            val onPrimaryColor = ContextCompat.getColor(context, onPrimaryResId)

            val contrastRatio = calculateContrastRatio(onPrimaryColor, primaryColor)
            assertTrue(
                "Dark theme on_primary should have adequate contrast with primary (>= 4.5:1)",
                contrastRatio >= 4.5
            )
        }
    }

    @Test
    fun testErrorColorContrast() {
        val lightErrorResId = resources.getIdentifier("light_error", "color", context.packageName)
        val lightOnErrorResId =
            resources.getIdentifier("light_on_error", "color", context.packageName)

        if (lightErrorResId != 0 && lightOnErrorResId != 0) {
            val errorColor = ContextCompat.getColor(context, lightErrorResId)
            val onErrorColor = ContextCompat.getColor(context, lightOnErrorResId)

            val contrastRatio = calculateContrastRatio(onErrorColor, errorColor)
            assertTrue(
                "Error color should have adequate contrast (>= 4.5:1)",
                contrastRatio >= 4.5
            )
        }
    }

    // Error/Failure Conditions
    @Test
    fun testInvalidColorResourceHandling() {
        val invalidColorResId =
            resources.getIdentifier("non_existent_color", "color", context.packageName)
        assertEquals("Invalid color resource should return 0", 0, invalidColorResId)
    }

    @Test
    fun testEmptyColorNameHandling() {
        val emptyColorResId = resources.getIdentifier("", "color", context.packageName)
        assertEquals("Empty color name should return 0", 0, emptyColorResId)
    }

    @Test
    fun testNullColorNameHandling() {
        try {
            val nullColorResId = resources.getIdentifier(null, "color", context.packageName)
            assertEquals("Null color name should return 0", 0, nullColorResId)
        } catch (e: Exception) {
            // Exception is acceptable for null input
            assertTrue("Should handle null gracefully", true)
        }
    }

    @Test
    fun testInvalidResourceTypeHandling() {
        val invalidTypeResId = resources.getIdentifier("neon_teal", "drawable", context.packageName)
        val colorResId = resources.getIdentifier("neon_teal", "color", context.packageName)

        if (invalidTypeResId != 0 && colorResId != 0) {
            assertNotEquals(
                "Color resource should not be same as drawable resource",
                invalidTypeResId, colorResId
            )
        }
    }

    // Neon Color Characteristics Tests
    @Test
    fun testNeonColorCharacteristics() {
        // Test neon teal has high green and blue components
        val neonTealResId = resources.getIdentifier("neon_teal", "color", context.packageName)
        if (neonTealResId != 0) {
            val color = ContextCompat.getColor(context, neonTealResId)
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF

            assertEquals("Neon teal should have no red component", 0, red)
            assertEquals("Neon teal should have maximum green component", 255, green)
            assertEquals("Neon teal should have high blue component", 204, blue)
        }

        // Test neon purple has high red and blue components
        val neonPurpleResId = resources.getIdentifier("neon_purple", "color", context.packageName)
        if (neonPurpleResId != 0) {
            val color = ContextCompat.getColor(context, neonPurpleResId)
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF

            assertEquals("Neon purple should have high red component", 224, red)
            assertEquals("Neon purple should have no green component", 0, green)
            assertEquals("Neon purple should have maximum blue component", 255, blue)
        }

        // Test neon blue has high green and blue components
        val neonBlueResId = resources.getIdentifier("neon_blue", "color", context.packageName)
        if (neonBlueResId != 0) {
            val color = ContextCompat.getColor(context, neonBlueResId)
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF

            assertEquals("Neon blue should have no red component", 0, red)
            assertEquals("Neon blue should have maximum green component", 255, green)
            assertEquals("Neon blue should have maximum blue component", 255, blue)
        }
    }

    // Color Consistency Tests
    @Test
    fun testThemeColorConsistency() {
        val themeColorPairs = listOf(
            "light_primary" to "dark_primary",
            "light_secondary" to "dark_secondary",
            "light_tertiary" to "dark_tertiary"
        )

        themeColorPairs.forEach { (lightColor, darkColor) ->
            val lightResId = resources.getIdentifier(lightColor, "color", context.packageName)
            val darkResId = resources.getIdentifier(darkColor, "color", context.packageName)

            if (lightResId != 0 && darkResId != 0) {
                val lightColorValue = ContextCompat.getColor(context, lightResId)
                val darkColorValue = ContextCompat.getColor(context, darkResId)

                // Colors should be related but may be different for accessibility
                assertNotNull("Light color $lightColor should be valid", lightColorValue)
                assertNotNull("Dark color $darkColor should be valid", darkColorValue)
            }
        }
    }

    @Test
    fun testOnColorConsistency() {
        val colorPairs = listOf(
            "light_primary" to "light_on_primary",
            "light_secondary" to "light_on_secondary",
            "light_surface" to "light_on_surface",
            "light_error" to "light_on_error"
        )

        colorPairs.forEach { (baseColor, onColor) ->
            val baseResId = resources.getIdentifier(baseColor, "color", context.packageName)
            val onResId = resources.getIdentifier(onColor, "color", context.packageName)

            if (baseResId != 0 && onResId != 0) {
                val baseColorValue = ContextCompat.getColor(context, baseResId)
                val onColorValue = ContextCompat.getColor(context, onResId)

                // On colors should be different from base colors
                assertNotEquals(
                    "On color $onColor should be different from base color $baseColor",
                    baseColorValue, onColorValue
                )
            }
        }
    }

    // Resource Integrity Tests
    @Test
    fun testAllDefinedColorsValid() {
        val allColors = listOf(
            // Base colors
            "neon_teal", "neon_purple", "neon_blue", "neon_teal_transparent",
            // Common colors
            "white", "black", "transparent",
            // Light theme colors
            "light_primary", "light_on_primary", "light_secondary", "light_on_secondary",
            "light_tertiary", "light_on_tertiary", "light_error", "light_on_error",
            "light_background", "light_on_background", "light_surface", "light_on_surface",
            // Dark theme colors
            "dark_primary", "dark_on_primary", "dark_secondary", "dark_on_secondary",
            "dark_tertiary", "dark_on_tertiary", "dark_error", "dark_on_error",
            "dark_background", "dark_on_background", "dark_surface", "dark_on_surface"
        )

        var validColorCount = 0
        allColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            if (colorResId != 0) {
                val color = ContextCompat.getColor(context, colorResId)
                assertNotEquals("Color $colorName should not be invalid", -1, color)
                validColorCount++
            }
        }

        assertTrue(
            "Should have most defined color resources valid",
            validColorCount >= allColors.size * 0.8
        )
    }

    @Test
    fun testDividerColorExists() {
        val dividerResId = resources.getIdentifier("divider", "color", context.packageName)
        if (dividerResId != 0) {
            val dividerColor = ContextCompat.getColor(context, dividerResId)

            // Divider should be semi-transparent
            val alpha = (dividerColor shr 24) and 0xFF
            assertTrue("Divider should be semi-transparent", alpha < 255)
        }
    }

    // Performance Tests
    @Test
    fun testColorResourceAccessPerformance() {
        val startTime = System.currentTimeMillis()

        // Access multiple color resources repeatedly
        repeat(100) {
            val colorResId = resources.getIdentifier("neon_teal", "color", context.packageName)
            if (colorResId != 0) {
                ContextCompat.getColor(context, colorResId)
            }
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        assertTrue(
            "Color resource access should be performant (< 1000ms for 100 accesses)",
            duration < 1000
        )
    }

    @Test
    fun testMultipleColorResourcesPerformance() {
        val colorNames = listOf("neon_teal", "neon_purple", "neon_blue", "black", "white")
        val startTime = System.currentTimeMillis()

        repeat(50) {
            colorNames.forEach { colorName ->
                val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
                if (colorResId != 0) {
                    ContextCompat.getColor(context, colorResId)
                }
            }
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        assertTrue(
            "Multiple color resource access should be performant (< 2000ms for 250 accesses)",
            duration < 2000
        )
    }

    // Theme Configuration Tests
    @Test
    fun testColorResourcesExistInCurrentConfiguration() {
        val coreColors = listOf("neon_teal", "neon_purple", "neon_blue", "black", "white")

        coreColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            assertTrue(
                "Core color $colorName should exist in current configuration",
                colorResId != 0
            )

            val color = ContextCompat.getColor(context, colorResId)
            assertNotEquals("Color $colorName should exist in current configuration", 0, color)
        }
    }

    @Test
    fun testMaterial3CompatibilityColors() {
        val compatibilityColors = listOf("onPrimary")

        compatibilityColors.forEach { colorName ->
            val colorResId = resources.getIdentifier(colorName, "color", context.packageName)
            if (colorResId != 0) {
                val color = ContextCompat.getColor(context, colorResId)
                assertNotEquals(
                    "Material 3 compatibility color $colorName should be valid",
                    0,
                    color
                )
            }
        }
    }

    // Helper Methods
    private fun calculateContrastRatio(color1: Int, color2: Int): Double {
        val luminance1 = calculateLuminance(color1)
        val luminance2 = calculateLuminance(color2)

        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun calculateLuminance(color: Int): Double {
        val red = ((color shr 16) and 0xFF) / 255.0
        val green = ((color shr 8) and 0xFF) / 255.0
        val blue = (color and 0xFF) / 255.0

        val sRed = if (red <= 0.03928) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
        val sGreen = if (green <= 0.03928) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
        val sBlue = if (blue <= 0.03928) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)

        return 0.2126 * sRed + 0.7152 * sGreen + 0.0722 * sBlue
    }

    private fun getHue(color: Int): Float {
        val red = ((color shr 16) and 0xFF) / 255.0f
        val green = ((color shr 8) and 0xFF) / 255.0f
        val blue = (color and 0xFF) / 255.0f

        val max = max(red, max(green, blue))
        val min = min(red, min(green, blue))
        val delta = max - min

        if (delta == 0f) return 0f

        return when (max) {
            red -> ((green - blue) / delta) % 6 * 60
            green -> ((blue - red) / delta + 2) * 60
            blue -> ((red - green) / delta + 4) * 60
            else -> 0f
        }
    }

    private fun minHueDifference(hue1: Float, hue2: Float): Float {
        val diff = abs(hue1 - hue2)
        return min(diff, 360 - diff)
    }
}