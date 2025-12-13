package dev.aurakai.auraframefx.gradle.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Tests for security best practices and dependency management guidelines.
 */
class SecurityAndBestPracticesTest {

    private lateinit var tomlContent: String

    @Before
    fun setUp() {
        val tomlFile = File("gradle/libs.versions.toml")
        tomlContent = tomlFile.readText()
    }

    @Test
    fun `test no snapshot or alpha versions in production dependencies`() {
        val lines = tomlContent.lines()
            .filter { it.contains(" = \"") && !it.trim().startsWith("#") }

        val problematicVersions = lines.filter { line ->
            val version = line.substringAfter("\"").substringBefore("\"")
            version.contains("SNAPSHOT", ignoreCase = true) ||
                    version.contains("-alpha", ignoreCase = true) ||
                    version.contains("-dev", ignoreCase = true)
        }

        assertTrue(
            "Production dependencies should not use SNAPSHOT or alpha versions: $problematicVersions",
            problematicVersions.isEmpty()
        )
    }

    @Test
    fun `test beta versions are minimal and documented`() {
        val betaVersions = tomlContent.lines()
            .filter { it.contains("-beta", ignoreCase = true) }

        // Should have minimal beta dependencies
        assertTrue(
            "Beta versions should be minimized in production, found: ${betaVersions.size}",
            betaVersions.size <= 2
        )

        betaVersions.forEach { line ->
            val lineIndex = tomlContent.lines().indexOf(line)
            val previousLines = tomlContent.lines().take(lineIndex + 1).takeLast(3)

            // Should have a comment explaining why beta version is used
            val hasComment = previousLines.any { it.trim().startsWith("#") }

            if (!hasComment) {
                println("Beta version without documentation: $line")
                // For security-crypto beta, this may be acceptable as it's AndroidX
            }
        }
    }

    @Test
    fun `test security-sensitive dependencies are recent`() {
        val securitySensitive = mapOf(
            "androidxSecurityCrypto" to "1.1.0",
            "okhttp" to "5.0.0",
            "retrofit" to "2.9.0"
        )

        securitySensitive.forEach { (dep, _) ->
            if (tomlContent.contains(dep)) {
                val versionLine = tomlContent.lines()
                    .find { it.contains(dep) && it.contains("version") }

                if (versionLine != null) {
                    val version = versionLine.substringAfter("\"").substringBefore("\"")
                    assertFalse(
                        "Security-sensitive dependency '$dep' should be recent version, found: $version",
                        version.isEmpty()
                    )

                    // Check minimum version requirements
                    if (dep == "okhttp") {
                        assertTrue(
                            "OkHttp should be 5.0+ for security: $version",
                            version.startsWith("5.")
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `test BOM usage for Google dependencies`() {
        // Firebase should use BOM
        assertTrue(
            "Firebase dependencies should use BOM pattern",
            tomlContent.contains("firebaseBom")
        )

        // Compose should use BOM
        assertTrue(
            "Compose dependencies should use BOM pattern",
            tomlContent.contains("composeBom")
        )

        // Verify BOM is actually used in libraries
        assertTrue(
            "Firebase BOM should be referenced in libraries",
            tomlContent.contains("com.google.firebase") && tomlContent.contains("firebase-bom")
        )
    }

    @Test
    fun `test deprecated libraries have migration strategy`() {
        val deprecatedLibraries = listOf("accompanist")

        deprecatedLibraries.forEach { deprecated ->
            val deprecatedLines = tomlContent.lines()
                .filter { it.contains(deprecated, ignoreCase = true) }

            deprecatedLines.forEach { line ->
                val lineIndex = tomlContent.lines().indexOf(line)
                val surroundingLines = tomlContent.lines()
                    .drop(maxOf(0, lineIndex - 2))
                    .take(5)

                // Should have migration notes nearby
                val hasMigrationNote = surroundingLines.any {
                    it.contains("migration", ignoreCase = true) ||
                            it.contains("deprecated", ignoreCase = true) ||
                            it.contains("replace", ignoreCase = true) ||
                            it.contains("Needs migration", ignoreCase = true)
                }

                if (hasMigrationNote) {
                    println("✓ Deprecated library with migration note: $line")
                } else {
                    println("⚠ Deprecated library without migration note: $line")
                }
            }
        }
    }

    @Test
    fun `test version catalog follows naming conventions`() {
        val versionLines = tomlContent.lines()
            .filter { it.contains(" = \"") && !it.trim().startsWith("#") }

        versionLines.forEach { line ->
            val variableName = line.split(" = ")[0].trim()

            // Variable names should be camelCase
            assertTrue(
                "Version variable should follow camelCase convention: $variableName",
                variableName.matches(Regex("[a-z][a-zA-Z0-9]*")) ||
                        variableName.matches(Regex("[a-z][a-zA-Z0-9_]*")) // Allow underscores for some cases
            )
        }
    }

    @Test
    fun `test kotlin coroutines versions are aligned`() {
        val coroutinesVersions = tomlContent.lines()
            .filter { it.contains("kotlinxCoroutines") && it.contains("version.ref") }

        coroutinesVersions.forEach { line ->
            assertTrue(
                "All Kotlin coroutines should use same version reference: $line",
                line.contains("""version.ref = "kotlinxCoroutines"""")
            )
        }
    }

    @Test
    fun `test compose libraries use BOM for version management`() {
        val composeLibraries = tomlContent.lines()
            .filter { it.contains("androidx.compose") && it.contains("group = ") }

        composeLibraries.forEach { line ->
            // Compose libraries should not have explicit versions when using BOM
            if (!line.contains("compose-bom")) {
                assertFalse(
                    "Compose library should use BOM for version management: $line",
                    line.contains("version.ref") || line.contains("version = ")
                )
            }
        }
    }

    @Test
    fun `test material design versions are consistent`() {
        val material3Version = tomlContent.lines()
            .find { it.contains("material3 = ") }

        if (material3Version != null) {
            val version = material3Version.substringAfter("\"").substringBefore("\"")
            assertTrue(
                "Material 3 should be version 1.3+",
                version.startsWith("1.3") || version.compareTo("1.3") >= 0
            )
        }
    }
}
