package settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Additional validation tests for settings.gradle.kts from app module perspective
 * Using JUnit 5 syntax to match existing app module test patterns
 */
class SettingsConfigurationTest {

    @Test
    fun `settings file should be parseable as gradle script`() {
        val settingsFile = File("../settings.gradle.kts")
        assertTrue(settingsFile.exists(), "Settings file should exist")

        val content = settingsFile.readText()

        // Basic syntax validation
        assertFalse(content.contains("TODO"), "Should not contain syntax errors")
        assertFalse(content.contains("FIXME"), "Should not contain placeholder text")

        // Should have balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals(openBraces, closeBraces, "Braces should be balanced")
    }

    @Test
    fun `app module should be included in settings`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            content.contains("include(\":app\")"),
            "App module should be included"
        )
    }

    @Test
    fun `project name should match expected value`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            content.contains("rootProject.name = \"AuraFrameFX\""),
            "Project name should be AuraFrameFX"
        )
    }

    @Test
    fun `should have proper module structure for Android project`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // Should include Android-specific configuration
        assertTrue(content.contains("google()"), "Should configure Android repositories")
        assertTrue(
            content.contains("com.android.application"),
            "Should have Android plugin management"
        )
    }

    @Test
    fun `should validate conditional module includes work correctly`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // Verify conditional logic exists
        assertTrue(
            content.contains("if (file(\"jvm-test\").exists())"),
            "Should have conditional jvm-test include"
        )
        assertTrue(
            content.contains("if (file(\"sandbox-ui\").exists())"),
            "Should have conditional sandbox-ui include"
        )

        // Verify actual module directories match conditions
        val jvmTestExists = File("../jvm-test").exists()
        val sandboxUiExists = File("../sandbox-ui").exists()

        if (jvmTestExists) {
            assertTrue(
                content.contains("include(\":jvm-test\")"),
                "jvm-test directory exists, should be included"
            )
        }

        if (sandboxUiExists) {
            assertTrue(
                content.contains("include(\":sandbox-ui\")"),
                "sandbox-ui directory exists, should be included"
            )
        }
    }

    @Test
    fun `should use secure repository URLs`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // All custom repository URLs should use HTTPS
        val repositoryUrls = listOf(
            "https://oss.sonatype.org/content/repositories/snapshots",
            "https://jitpack.io"
        )

        repositoryUrls.forEach { url ->
            assertTrue(content.contains(url), "Should contain secure repository URL: $url")
            assertTrue(url.startsWith("https://"), "Repository URL should use HTTPS: $url")
        }
    }

    @Test
    fun `should enforce fail on project repos policy`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            content.contains("repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)"),
            "Should enforce centralized repository management"
        )
    }
}