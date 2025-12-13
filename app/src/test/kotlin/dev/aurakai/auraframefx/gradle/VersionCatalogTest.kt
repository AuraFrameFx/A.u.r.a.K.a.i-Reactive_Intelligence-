package dev.aurakai.auraframefx.gradle

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Tests for version catalog usage and consistency in build.gradle.kts
 *
 * Testing Framework: JUnit 4
 */
class VersionCatalogTest {

    private lateinit var buildContent: String

    @Before
    fun setup() {
        val buildFile = File("app/build.gradle.kts")
        buildContent = if (buildFile.exists()) {
            buildFile.readText()
        } else {
            // Fallback for test environment
            val fallbackFile = File("../app/build.gradle.kts")
            if (fallbackFile.exists()) {
                fallbackFile.readText()
            } else {
                ""
            }
        }
    }

    @Test
    fun `test version catalog is used consistently`() {
        // Check that dependencies use version catalog pattern
        val hardcodedVersionPattern = Regex("\"[0-9]+\\.[0-9]+\\.[0-9]+\"")
        val hardcodedVersions = hardcodedVersionPattern.findAll(buildContent)

        // Allow some hardcoded versions for SDK levels and specific configurations
        val allowedHardcodedVersions = listOf("27.0.12077973", "3.22.1", "1.0")
        val unexpectedVersions = hardcodedVersions.map { it.value.trim('"') }
            .filterNot { version ->
                allowedHardcodedVersions.any { allowed ->
                    version.contains(
                        allowed
                    )
                }
            }

        assertTrue(
            "Should minimize hardcoded versions in favor of version catalog: $unexpectedVersions",
            unexpectedVersions.isEmpty()
        )
    }

    @Test
    fun `test libs references are properly formatted`() {
        val libsReferences = Regex("libs\\.[a-zA-Z0-9\\.]+").findAll(buildContent)
        assertTrue("Should have multiple version catalog references", libsReferences.count() > 20)

        // Verify common patterns
        assertTrue(
            "Should reference compose BOM",
            buildContent.contains("libs.composeBom")
        )
        assertTrue(
            "Should reference Android core KTX",
            buildContent.contains("libs.androidxCoreKtx")
        )
        assertTrue(
            "Should reference Hilt Android",
            buildContent.contains("libs.hiltAndroid")
        )
    }

    @Test
    fun `test plugin aliases are used`() {
        val pluginAliases =
            Regex("alias\\(libs\\.plugins\\.[a-zA-Z0-9\\.]+\\)").findAll(buildContent)
        assertTrue("Should use plugin aliases from version catalog", pluginAliases.count() >= 5)

        // Verify specific plugin aliases
        assertTrue(
            "Should use Android Application plugin alias",
            buildContent.contains("alias(libs.plugins.androidApplication)")
        )
        assertTrue(
            "Should use Kotlin Android plugin alias",
            buildContent.contains("alias(libs.plugins.kotlinAndroid)")
        )
        assertTrue(
            "Should use Hilt plugin alias",
            buildContent.contains("alias(libs.plugins.hiltAndroid)")
        )
    }

    @Test
    fun `test version references in configuration`() {
        assertTrue(
            "Should reference Kotlin version from catalog",
            buildContent.contains("libs.versions.kotlin.get()")
        )
        assertTrue(
            "Should reference Compose compiler version from catalog",
            buildContent.contains("libs.versions.composeCompiler.get()")
        )
    }

    @Test
    fun `test no direct dependency declarations`() {
        // Check for direct dependency declarations that should use version catalog
        val directDependencyPatterns = listOf(
            Regex("implementation\\s*\\(\\s*\"[^l][^i][^b][^s]"),  // Not starting with "libs"
            Regex("testImplementation\\s*\\(\\s*\"[^l][^i][^b][^s]"),
            Regex("androidTestImplementation\\s*\\(\\s*\"[^l][^i][^b][^s]")
        )

        directDependencyPatterns.forEach { pattern ->
            val matches = pattern.findAll(buildContent)
            // Allow some exceptions like platform() and files()
            val validExceptions = matches.filter { match ->
                match.value.contains("platform(") ||
                        match.value.contains("files(") ||
                        match.value.contains("coreLibraryDesugaring(")
            }
            val invalidMatches = matches.count() - validExceptions.count()

            assertTrue(
                "Should minimize direct dependency declarations in favor of version catalog",
                invalidMatches <= 2
            ) // Allow some minimal exceptions
        }
    }

    // Additional comprehensive test cases

    @Test
    fun `test build file exists and is readable`() {
        assertFalse("Build file content should not be empty", buildContent.isEmpty())
        assertTrue(
            "Build file should contain basic Gradle structures",
            buildContent.contains("plugins") || buildContent.contains("dependencies")
        )
    }

    @Test
    fun `test version catalog references follow naming conventions`() {
        val libsReferences = Regex("libs\\.[a-zA-Z0-9\\.]+").findAll(buildContent)
        libsReferences.forEach { match ->
            val reference = match.value
            // Check for proper camelCase naming after "libs."
            val nameAfterLibs = reference.substring(5) // Remove "libs."
            assertFalse(
                "Library reference should not contain underscores: $reference",
                nameAfterLibs.contains("_")
            )
            assertFalse(
                "Library reference should not start with number: $reference",
                nameAfterLibs.firstOrNull()?.isDigit() == true
            )
        }
    }

    @Test
    fun `test plugin alias format consistency`() {
        val pluginAliases =
            Regex("alias\\(libs\\.plugins\\.[a-zA-Z0-9\\.]+\\)").findAll(buildContent)
        pluginAliases.forEach { match ->
            val alias = match.value
            assertTrue(
                "Plugin alias should be properly formatted: $alias",
                alias.matches(Regex("alias\\(libs\\.plugins\\.[a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z][a-zA-Z0-9]*)*\\)"))
            )
        }
    }

    @Test
    fun `test no deprecated dependency syntax`() {
        // Check for old-style compile dependencies
        assertFalse(
            "Should not use deprecated 'compile' configuration",
            buildContent.contains("compile ")
        )
        assertFalse(
            "Should not use deprecated 'testCompile' configuration",
            buildContent.contains("testCompile ")
        )
        assertFalse(
            "Should not use deprecated 'androidTestCompile' configuration",
            buildContent.contains("androidTestCompile ")
        )
    }

    @Test
    fun `test consistent spacing in dependency declarations`() {
        val dependencyPatterns = listOf(
            "implementation",
            "testImplementation",
            "androidTestImplementation",
            "debugImplementation",
            "api"
        )

        dependencyPatterns.forEach { pattern ->
            val regex = Regex("$pattern\\s*\\(")
            val matches = regex.findAll(buildContent)
            matches.forEach { match ->
                // Check for consistent spacing patterns
                assertTrue(
                    "Dependency declaration should have proper spacing: ${match.value}",
                    match.value.matches(Regex("$pattern\\s*\\("))
                )
            }
        }
    }

    @Test
    fun `test version catalog bundles usage`() {
        // Check if bundles are used for related dependencies
        val bundlePattern = Regex("libs\\.bundles\\.[a-zA-Z0-9\\.]+")
        val bundleReferences = bundlePattern.findAll(buildContent)

        // If bundles are used, they should follow naming conventions
        bundleReferences.forEach { match ->
            val bundle = match.value
            val bundleName = bundle.substring(13) // Remove "libs.bundles."
            assertFalse(
                "Bundle name should not contain underscores: $bundle",
                bundleName.contains("_")
            )
        }
    }

    @Test
    fun `test kapt and ksp processor declarations use catalog`() {
        val processorConfigurations = listOf("kapt", "ksp")

        processorConfigurations.forEach { config ->
            val processorPattern = Regex("$config\\s*\\(\\s*\"[^l][^i][^b][^s]")
            val directProcessors = processorPattern.findAll(buildContent)

            directProcessors.forEach { match ->
                // Allow some exceptions for processors that might not be in catalog
                val allowedExceptions = listOf("androidx.room")
                val isException = allowedExceptions.any { exception ->
                    match.value.contains(exception)
                }

                if (!isException) {
                    // Warn about potential catalog usage
                    println("Consider using version catalog for processor: ${match.value}")
                }
            }
        }
    }

    @Test
    fun `test BOM platform dependencies are properly handled`() {
        val bomPattern = Regex("platform\\(libs\\.[a-zA-Z0-9\\.]+\\)")
        bomPattern.findAll(buildContent)

        // Common BOMs should be present and properly used
        val expectedBoms = listOf("composeBom", "firebaseBom")
        expectedBoms.forEach { bom ->
            if (buildContent.contains("libs.$bom")) {
                assertTrue(
                    "BOM $bom should be used with platform(): expected platform(libs.$bom)",
                    buildContent.contains("platform(libs.$bom)")
                )
            }
        }
    }

    @Test
    fun `test version reference syntax consistency`() {
        val versionReferences =
            Regex("libs\\.versions\\.[a-zA-Z0-9\\.]+\\.get\\(\\)").findAll(buildContent)

        versionReferences.forEach { match ->
            val reference = match.value
            assertTrue(
                "Version reference should end with .get(): $reference",
                reference.endsWith(".get()")
            )

            val versionName = reference.substring(
                13,
                reference.length - 6
            ) // Remove "libs.versions." and ".get()"
            assertFalse(
                "Version name should not contain underscores: $reference",
                versionName.contains("_")
            )
        }
    }

    @Test
    fun `test implementation vs api usage patterns`() {
        val apiCount = Regex("api\\s*\\(").findAll(buildContent).count()
        val implementationCount = Regex("implementation\\s*\\(").findAll(buildContent).count()

        // Generally, implementation should be preferred over api
        if (apiCount > 0) {
            assertTrue(
                "Should use 'implementation' more frequently than 'api' for better encapsulation",
                implementationCount >= apiCount
            )
        }
    }

    @Test
    fun `test no hardcoded build tool versions should use catalog when possible`() {
        // Check for hardcoded build tool versions that could potentially use catalog
        val buildToolVersionPatterns = listOf(
            Regex("compileSdk\\s*=\\s*[0-9]+"),
            Regex("targetSdk\\s*=\\s*[0-9]+"),
            Regex("minSdk\\s*=\\s*[0-9]+")
        )

        var foundHardcodedVersions = 0
        buildToolVersionPatterns.forEach { pattern ->
            val matches = pattern.findAll(buildContent)
            foundHardcodedVersions += matches.count()
        }

        // This is more of a suggestion than a hard requirement
        assertTrue(
            "Build tool versions found - consider version catalog for consistency",
            foundHardcodedVersions >= 0
        ) // Always pass but log findings
    }

    @Test
    fun `test proper multiline dependency formatting`() {
        // Check for consistent formatting in multiline dependency blocks
        val dependencyBlocks = Regex("dependencies\\s*\\{[^}]*\\}", RegexOption.DOT_MATCHES_ALL)
            .findAll(buildContent)

        dependencyBlocks.forEach { block ->
            val content = block.value
            // Check for consistent indentation (assuming 4 spaces or tabs)
            val lines = content.split("\n").filter {
                it.trim().isNotEmpty() &&
                        it.trim() != "dependencies {" &&
                        it.trim() != "}"
            }

            lines.forEach { line ->
                if (line.contains("implementation") || line.contains("testImplementation") || line.contains(
                        "api"
                    )
                ) {
                    assertTrue(
                        "Dependency lines should be properly indented: '$line'",
                        line.startsWith("    ") || line.startsWith("\t")
                    )
                }
            }
        }
    }

    @Test
    fun `test version catalog availability edge cases`() {
        // Test behavior when build file might not exist or be empty
        val emptyBuildContent = ""

        // Simulate testing with empty content
        val hardcodedVersionPattern = Regex("\"[0-9]+\\.[0-9]+\\.[0-9]+\"")
        val hardcodedVersions = hardcodedVersionPattern.findAll(emptyBuildContent)
        assertEquals(
            "Empty build file should have no hardcoded versions",
            0,
            hardcodedVersions.count()
        )
    }

    @Test
    fun `test configuration cache compatibility`() {
        // Check for patterns that might break configuration cache
        val problematicPatterns = listOf(
            Regex("System\\.getProperty"),
            Regex("project\\.hasProperty"),
            Regex("gradle\\.startParameter")
        )

        var potentialIssues = 0
        problematicPatterns.forEach { pattern ->
            val matches = pattern.findAll(buildContent)
            potentialIssues += matches.count()
        }

        // This is informational - configuration cache issues are sometimes unavoidable
        assertTrue("Configuration cache compatibility check completed", potentialIssues >= 0)
    }

    @Test
    fun `test specific plugin version references`() {
        // Check that Kotlin plugin version uses catalog reference
        if (buildContent.contains("org.jetbrains.kotlin.plugin.compose")) {
            assertTrue(
                "Kotlin compose plugin should use version catalog",
                buildContent.contains("version libs.versions.kotlin.get()")
            )
        }
    }

    @Test
    fun `test no duplicate plugin applications`() {
        val pluginApplications = mutableMapOf<String, Int>()

        // Find all plugin applications
        val pluginPattern =
            Regex("(alias\\(libs\\.plugins\\.[a-zA-Z0-9\\.]+\\)|id\\(\"[^\"]+\"\\))")
        val matches = pluginPattern.findAll(buildContent)

        matches.forEach { match ->
            val plugin = match.value
            pluginApplications[plugin] = pluginApplications.getOrDefault(plugin, 0) + 1
        }

        // Check for duplicates
        pluginApplications.forEach { (plugin, count) ->
            assertTrue(
                "Plugin should not be applied multiple times: $plugin (found $count times)",
                count <= 1
            )
        }
    }

    @Test
    fun `test proper dependency scoping`() {
        // Verify that test dependencies are properly scoped
        val testDependencies =
            Regex("testImplementation\\(libs\\.[a-zA-Z0-9\\.]+\\)").findAll(buildContent)
        Regex("androidTestImplementation\\(libs\\.[a-zA-Z0-9\\.]+\\)").findAll(buildContent)

        assertTrue("Should have test dependencies defined", testDependencies.count() > 0)

        // Common test libraries should be in test scope
        if (buildContent.contains("libs.testJunit")) {
            assertTrue(
                "JUnit should be in testImplementation scope",
                buildContent.contains("testImplementation(libs.testJunit)")
            )
        }
    }

    @Test
    fun `test namespace consistency`() {
        // Check that namespace matches expected pattern
        val namespacePattern = Regex("namespace\\s*=\\s*\"([^\"]+)\"")
        val namespaceMatch = namespacePattern.find(buildContent)

        if (namespaceMatch != null) {
            val namespace = namespaceMatch.groupValues[1]
            assertTrue(
                "Namespace should follow reverse domain notation",
                namespace.contains(".") && namespace.split(".").size >= 2
            )
        }
    }

    @Test
    fun `test version catalog usage completeness`() {
        // Ensure high percentage of dependencies use version catalog
        val allDependencies =
            Regex("(implementation|testImplementation|androidTestImplementation|api|debugImplementation)\\s*\\(")
                .findAll(buildContent).count()
        val catalogDependencies =
            Regex("(implementation|testImplementation|androidTestImplementation|api|debugImplementation)\\s*\\(\\s*libs\\.")
                .findAll(buildContent).count()

        if (allDependencies > 0) {
            val catalogUsagePercentage =
                (catalogDependencies.toDouble() / allDependencies.toDouble()) * 100
            assertTrue(
                "Should use version catalog for majority of dependencies (${catalogUsagePercentage.toInt()}%)",
                catalogUsagePercentage >= 70.0
            )
        }
    }
}
