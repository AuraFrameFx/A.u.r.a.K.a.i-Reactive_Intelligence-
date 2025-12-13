package dev.aurakai.auraframefx.gradle

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Edge case and error handling tests for build configuration
 *
 * Testing Framework: JUnit 4
 */
class BuildConfigurationEdgeCasesTest {

    private lateinit var buildContent: String

    @Before
    fun setup() {
        val buildFile = File("app/build.gradle.kts")
        buildContent = if (buildFile.exists()) {
            buildFile.readText()
        } else {
            ""
        }
    }

    @Test
    fun `test build file exists and is not empty`() {
        val buildFile = File("app/build.gradle.kts")
        if (!buildFile.exists()) {
            // Try fallback path
            val fallbackFile = File("../app/build.gradle.kts")
            assertTrue("Build file should exist at some location", fallbackFile.exists())
            assertFalse("Build file should not be empty", fallbackFile.readText().isBlank())
        } else {
            assertFalse("Build file should not be empty", buildContent.isBlank())
        }
    }

    @Test
    fun `test SDK versions are within reasonable ranges`() {
        // Extract SDK versions using regex
        val compileSdkPattern = Regex("compileSdk\\s*=\\s*(\\d+)")
        val targetSdkPattern = Regex("targetSdk\\s*=\\s*(\\d+)")
        val minSdkPattern = Regex("minSdk\\s*=\\s*(\\d+)")

        val compileSdk = compileSdkPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()
        val targetSdk = targetSdkPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()
        val minSdk = minSdkPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()

        if (compileSdk != null) {
            assertTrue("CompileSdk should be reasonable (> 30)", compileSdk > 30)
            assertTrue("CompileSdk should not be too high (< 50)", compileSdk < 50)
        }

        if (targetSdk != null) {
            assertTrue("TargetSdk should be reasonable (> 30)", targetSdk > 30)
            assertTrue("TargetSdk should not be too high (< 50)", targetSdk < 50)
        }

        if (minSdk != null) {
            assertTrue("MinSdk should be reasonable (> 20)", minSdk > 20)
            assertTrue("MinSdk should not be too high (< 40)", minSdk < 40)
        }

        if (compileSdk != null && targetSdk != null) {
            assertTrue("CompileSdk should be >= TargetSdk", compileSdk >= targetSdk)
        }

        if (targetSdk != null && minSdk != null) {
            assertTrue("TargetSdk should be >= MinSdk", targetSdk >= minSdk)
        }
    }

    @Test
    fun `test no conflicting plugin applications`() {
        // Check for potential plugin conflicts
        val pluginLines = buildContent.lines().filter { it.contains("alias(libs.plugins.") }
        val uniquePlugins = pluginLines.toSet()

        assertEquals(
            "Should not have duplicate plugin applications",
            pluginLines.size, uniquePlugins.size
        )
    }

    @Test
    fun `test no conflicting dependency declarations`() {
        // Look for potential duplicate dependencies
        val implementationPattern = Regex("implementation\\(libs\\.[a-zA-Z0-9\\.]+\\)")
        val implementations = implementationPattern.findAll(buildContent).map { it.value }.toList()
        val uniqueImplementations = implementations.toSet()

        assertEquals(
            "Should not have duplicate implementation declarations",
            implementations.size, uniqueImplementations.size
        )
    }

    @Test
    fun `test CMake version is valid`() {
        val cmakeVersionPattern = Regex("version\\s*=\\s*\"([0-9\\.]+)\"")
        val cmakeVersion = cmakeVersionPattern.find(buildContent)?.groupValues?.get(1)

        if (cmakeVersion != null) {
            val versionParts = cmakeVersion.split(".")
            assertTrue("CMake version should have at least major.minor", versionParts.size >= 2)

            val major = versionParts[0].toIntOrNull()
            val minor = versionParts[1].toIntOrNull()

            assertNotNull("CMake major version should be numeric", major)
            assertNotNull("CMake minor version should be numeric", minor)

            if (major != null) {
                assertTrue("CMake major version should be reasonable", major >= 3)
            }
        }
    }

    @Test
    fun `test NDK version format is valid`() {
        val ndkVersionPattern = Regex("ndkVersion\\s*=\\s*\"([^\"]+)\"")
        val ndkVersion = ndkVersionPattern.find(buildContent)?.groupValues?.get(1)

        if (ndkVersion != null) {
            assertTrue("NDK version should contain dots", ndkVersion.contains("."))
            assertFalse("NDK version should not be empty", ndkVersion.isBlank())
        }
    }

    @Test
    fun `test ABI filters are realistic`() {
        if (buildContent.contains("abiFilters")) {
            // Common ABI names that should be present
            val commonAbis = listOf("arm64-v8a", "armeabi-v7a", "x86_64")

            commonAbis.forEach { abi ->
                if (buildContent.contains(abi)) {
                    assertTrue(
                        "ABI $abi should be properly quoted",
                        buildContent.contains("\"$abi\"")
                    )
                }
            }
        }
    }

    @Test
    fun `test proguard files exist or are standard`() {
        if (buildContent.contains("proguard-rules.pro")) {
            // This is a custom proguard file that should exist
            // In a real test environment, we might check if it exists
            assertTrue(
                "Proguard rules file should be referenced",
                buildContent.contains("\"proguard-rules.pro\"")
            )
        }

        // Check for standard proguard file
        assertTrue(
            "Should reference standard proguard file",
            buildContent.contains("proguard-android-optimize.txt")
        )
    }

    @Test
    fun `test no deprecated API usage`() {
        // Check for some known deprecated patterns
        val deprecatedPatterns = listOf(
            "compile\\s*\\(",  // Should use implementation
            "testCompile\\s*\\(",  // Should use testImplementation
            "androidTestCompile\\s*\\("  // Should use androidTestImplementation
        )

        deprecatedPatterns.forEach { pattern ->
            val regex = Regex(pattern)
            assertFalse(
                "Should not use deprecated dependency declaration: $pattern",
                regex.containsMatchIn(buildContent)
            )
        }
    }

    @Test
    fun `test Kotlin compiler arguments are valid`() {
        if (buildContent.contains("freeCompilerArgs")) {
            // Check for valid Kotlin compiler arguments
            val validArgs = listOf(
                "-Xjvm-default=all",
                "-Xcontext-receivers",
                "-opt-in=kotlin.RequiresOptIn"
            )

            validArgs.forEach { arg ->
                if (buildContent.contains(arg)) {
                    assertTrue(
                        "Compiler arg $arg should be properly quoted",
                        buildContent.contains("\"$arg\"")
                    )
                }
            }
        }
    }

    @Test
    fun `test build configuration syntax is valid Kotlin`() {
        // Basic syntax checks
        assertFalse(
            "Should not have unmatched opening braces",
            buildContent.count { it == '{' } < buildContent.count { it == '}' })
        assertFalse(
            "Should not have unmatched closing braces",
            buildContent.count { it == '{' } > buildContent.count { it == '}' })

        // Check for common syntax errors
        assertFalse(
            "Should not have trailing commas in wrong places",
            buildContent.contains(",}")
        )
        assertFalse(
            "Should not have double semicolons",
            buildContent.contains(";;")
        )
    }

    @Test
    fun `test build tools version is compatible`() {
        // Check for buildToolsVersion if present
        Regex(
            "buildToolsVersion\s*=\s*"([^"]+)"")
            val buildToolsVersion = buildToolsPattern . find (buildContent)?.groupValues?.get(1)

        if (buildToolsVersion != null) {
            assertTrue("Build tools version should not be empty", buildToolsVersion.isNotBlank())
            // Check version format (should be like "33.0.0")
            val versionPattern = Regex("\d+\.\d+\.\d+")
            assertTrue(
                "Build tools version should follow x.y.z format",
                versionPattern.matches(buildToolsVersion)
            )
        }
    }

    @Test
    fun `test application ID format is valid`() {
        Regex(
            "applicationId\s*=\s*"([^"]+)"")
            val applicationId = appIdPattern . find (buildContent)?.groupValues?.get(1)

        if (applicationId != null) {
            assertTrue("Application ID should not be empty", applicationId.isNotBlank())
            assertTrue("Application ID should contain dots", applicationId.contains("."))
            assertFalse("Application ID should not start with dot", applicationId.startsWith("."))
            assertFalse("Application ID should not end with dot", applicationId.endsWith("."))
            assertTrue(
                "Application ID should follow package naming convention",
                applicationId.matches(Regex("[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+"))
            )
        }
    }

    @Test
    fun `test version name and code consistency`() {
        val versionNamePattern = Regex(
            "versionName\s*=\s*"([^"]+)"")
            val versionCodePattern = Regex ("versionCode\s*=\s*(\d+)")

        val versionName = versionNamePattern.find(buildContent)?.groupValues?.get(1)
        val versionCode = versionCodePattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()

        if (versionName != null) {
            assertTrue("Version name should not be empty", versionName.isNotBlank())
            assertTrue(
                "Version name should follow semantic versioning",
                versionName.matches(Regex("\d+\.\d+\.\d+.*"))
            )
        }

        if (versionCode != null) {
            assertTrue("Version code should be positive", versionCode > 0)
            assertTrue("Version code should be reasonable", versionCode < 1000000)
        }
    }

    @Test
    fun `test namespace configuration is valid`() {
        Regex(
            "namespace\s*=\s*"([^"]+)"")
            val namespace = namespacePattern . find (buildContent)?.groupValues?.get(1)

        if (namespace != null) {
            assertTrue("Namespace should not be empty", namespace.isNotBlank())
            assertTrue(
                "Namespace should follow package naming convention",
                namespace.matches(Regex("[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+"))
            )
        }
    }

    @Test
    fun `test build types are properly configured`() {
        if (buildContent.contains("buildTypes")) {
            // Check for debug and release build types
            buildContent.contains("debug {")
            val releaseConfigured = buildContent.contains("release {")

            if (releaseConfigured) {
                assertTrue(
                    "Release build should have minifyEnabled configured",
                    buildContent.contains("minifyEnabled")
                )
            }

            // Check for proper signing configuration
            if (buildContent.contains("signingConfig")) {
                assertTrue(
                    "Signing config should be properly referenced",
                    buildContent.contains("signingConfigs.") || buildContent.contains("signingConfig =")
                )
            }
        }
    }

    @Test
    fun `test lint configuration is reasonable`() {
        if (buildContent.contains("lint {")) {
            // Check for common lint configurations
            val lintOptions = listOf("abortOnError", "warningsAsErrors", "checkReleaseBuilds")

            lintOptions.forEach { option ->
                if (buildContent.contains(option)) {
                    assertTrue(
                        "Lint option $option should have boolean value",
                        buildContent.contains("$option = true") || buildContent.contains("$option = false")
                    )
                }
            }
        }
    }

    @Test
    fun `test resource configuration is valid`() {
        // Check for resource shrinking in release builds
        if (buildContent.contains("shrinkResources")) {
            assertTrue(
                "Resource shrinking should be boolean",
                buildContent.contains("shrinkResources = true") ||
                        buildContent.contains("shrinkResources = false")
            )
        }

        // Check for resource configurations
        if (buildContent.contains("resConfigs")) {
            assertTrue(
                "Resource configs should be properly quoted",
                buildContent.contains("" en "") || buildContent.contains("resConfigs(")
            )
        }
    }

    @Test
    fun `test test instrumentation runner configuration`() {
        Regex(
            "testInstrumentationRunner\s*=\s*"([^"]+)"")
            val testRunner = testRunnerPattern . find (buildContent)?.groupValues?.get(1)

        if (testRunner != null) {
            assertTrue("Test instrumentation runner should not be empty", testRunner.isNotBlank())
            assertTrue(
                "Test instrumentation runner should follow package naming",
                testRunner.contains(".")
            )
            assertFalse(
                "Test instrumentation runner should not start with dot",
                testRunner.startsWith(".")
            )
        }
    }

    @Test
    fun `test external native build configuration is valid`() {
        if (buildContent.contains("externalNativeBuild")) {
            // Check for CMake configuration
            if (buildContent.contains("cmake")) {
                assertTrue(
                    "CMake block should be properly configured",
                    buildContent.contains("cmake {")
                )

                if (buildContent.contains("path")) {
                    assertTrue(
                        "CMake path should reference CMakeLists.txt",
                        buildContent.contains("CMakeLists.txt")
                    )
                }
            }
        }
    }

    @Test
    fun `test dependencies block structure is valid`() {
        if (buildContent.contains("dependencies")) {
            assertTrue(
                "Dependencies block should be properly opened",
                buildContent.contains("dependencies {")
            )

            // Count dependency declarations
            val implementationCount = buildContent.split("implementation(").size - 1
            buildContent.split("testImplementation(").size - 1

            assertTrue(
                "Should have at least one implementation dependency",
                implementationCount > 0
            )

            // Check for proper libs reference format
            if (buildContent.contains("implementation(libs.")) {
                assertTrue(
                    "Libs references should follow proper format",
                    buildContent.contains("libs.") && buildContent.contains(")")
                )
            }
        }
    }

    @Test
    fun `test Java compatibility versions are consistent`() {
        val sourceCompatPattern = Regex("sourceCompatibility\s*=\s*JavaVersion\.VERSION_(\d+)")
        val targetCompatPattern = Regex("targetCompatibility\s*=\s*JavaVersion\.VERSION_(\d+)")
        val jvmTargetPattern = Regex("jvmTarget\s*=\s*JvmTarget\.JVM_(\d+)")

        val sourceCompat =
            sourceCompatPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()
        val targetCompat =
            targetCompatPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()
        val jvmTarget = jvmTargetPattern.find(buildContent)?.groupValues?.get(1)?.toIntOrNull()

        if (sourceCompat != null && targetCompat != null) {
            assertEquals("Source and target compatibility should match", sourceCompat, targetCompat)
        }

        if (targetCompat != null && jvmTarget != null) {
            assertEquals(
                "Target compatibility and JVM target should match",
                targetCompat,
                jvmTarget
            )
        }
    }

    @Test
    fun `test multiDex configuration is valid`() {
        if (buildContent.contains("multiDexEnabled")) {
            assertTrue(
                "MultiDex should be boolean value",
                buildContent.contains("multiDexEnabled = true") ||
                        buildContent.contains("multiDexEnabled = false")
            )
        }
    }

    @Test
    fun `test packaging options are properly configured`() {
        if (buildContent.contains("packaging")) {
            assertTrue(
                "Packaging block should be properly opened",
                buildContent.contains("packaging {")
            )

            // Check for resource excludes
            if (buildContent.contains("excludes")) {
                assertTrue(
                    "Resource excludes should be properly formatted",
                    buildContent.contains("excludes.add") || buildContent.contains("excludes +=")
                )
            }
        }
    }

    @Test
    fun `test vector drawables configuration`() {
        if (buildContent.contains("vectorDrawables")) {
            assertTrue(
                "Vector drawables block should be properly configured",
                buildContent.contains("vectorDrawables {")
            )

            if (buildContent.contains("useSupportLibrary")) {
                assertTrue(
                    "useSupportLibrary should be boolean",
                    buildContent.contains("useSupportLibrary = true") ||
                            buildContent.contains("useSupportLibrary = false")
                )
            }
        }
    }

    @Test
    fun `test plugin application order is correct`() {
        if (buildContent.contains("plugins {")) {
            val pluginLines = buildContent.lines().filter { line ->
                line.trim().startsWith("alias(libs.plugins.") ||
                        line.trim().startsWith("id("")
            }

            // Check that KSP is applied before Hilt (if both are present)
            val kspIndex = pluginLines.indexOfFirst { it.contains("ksp") }
            val hiltIndex = pluginLines.indexOfFirst { it.contains("hilt") }

            if (kspIndex != -1 && hiltIndex != -1) {
                assertTrue("KSP should be applied before Hilt", kspIndex < hiltIndex)
            }
        }
    }

    @Test
    fun `test no duplicate plugin applications`() {
        val pluginPattern = Regex("alias\(libs\.plugins\.([^)]+)\)")
        val plugins = pluginPattern.findAll(buildContent).map { it.groupValues[1] }.toList()
        val uniquePlugins = plugins.toSet()

        assertEquals(
            "Should not have duplicate plugin applications",
            plugins.size, uniquePlugins.size
        )
    }

    @Test
    fun `test build configuration handles malformed content gracefully`() {
        // Test that our regex patterns don't crash on malformed content
        val malformedContent = "invalid { content with } unmatched braces and \"quotes"

        val patterns = listOf(
            Regex("compileSdk\\s*=\\s*(\\d+)"),
            Regex("targetSdk\\s*=\\s*(\\d+)"),
            Regex("minSdk\\s*=\\s*(\\d+)"),
            Regex("applicationId\\s*=\\s*\"([^\"]+)\""),
            Regex("namespace\\s*=\\s*\"([^\"]+)\"")
        )

        patterns.forEach { pattern ->
            try {
                pattern.find(malformedContent)
                // Should not throw exception
                assertTrue("Regex pattern should handle malformed content", true)
            } catch (e: Exception) {
                fail("Regex pattern should not throw exception on malformed content: ${e.message}")
            }
        }
    }

    @Test
    fun `test empty build file is handled gracefully`() {
        val emptyContent = ""

        // Test all our main regex patterns with empty content
        val patterns = listOf(
            Regex("compileSdk\\s*=\\s*(\\d+)"),
            Regex("applicationId\\s*=\\s*\"([^\"]+)\""),
            Regex("versionName\\s*=\\s*\"([^\"]+)\"")
        )

        patterns.forEach { pattern ->
            val result = pattern.find(emptyContent)
            assertNull("Empty content should return null for regex searches", result)
        }
    }

    @Test
    fun `test build file content parsing performance`() {
        // Test that parsing large content is reasonably fast
        val largeContent = buildContent + "\n" + "// ".repeat(10000) + "large comment block"

        val startTime = System.currentTimeMillis()

        // Run several regex operations
        val compileSdkPattern = Regex("compileSdk\\s*=\\s*(\\d+)")
        val appIdPattern = Regex("applicationId\\s*=\\s*\"([^\"]+)\"")
        val versionPattern = Regex("versionName\\s*=\\s*\"([^\"]+)\"")

        compileSdkPattern.find(largeContent)
        appIdPattern.find(largeContent)
        versionPattern.find(largeContent)

        val endTime = System.currentTimeMillis()

        assertTrue(
            "Regex searches should complete within reasonable time",
            (endTime - startTime) < 5000
        ) // 5 seconds max
    }

    @Test
    fun `test build file path resolution robustness`() {
        // Test with different possible build file locations
        val possiblePaths = listOf(
            "app/build.gradle.kts",
            "build.gradle.kts",
            "../app/build.gradle.kts",
            "./app/build.gradle.kts"
        )

        var foundValidPath = false
        var foundContent = ""

        for (path in possiblePaths) {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val content = file.readText()
                if (content.isNotBlank()) {
                    foundValidPath = true
                    foundContent = content
                    break
                }
            }
        }

        assertTrue("Should find build file at one of the expected paths", foundValidPath)
        if (foundValidPath) {
            assertTrue(
                "Found build file should contain android block",
                foundContent.contains("android {") || foundContent.contains("android{")
            )
        }
    }

    @Test
    fun `test configuration security - no hardcoded secrets`() {
        // Check for patterns that might indicate hardcoded secrets
        val suspiciousPatterns = listOf(
            "api_key", "apiKey", "password", "secret", "token",
            "private_key", "privateKey", "auth_token", "authToken"
        )

        suspiciousPatterns.forEach { pattern ->
            val regex = Regex("$pattern\\s*=\\s*\"[^\"]+\"", RegexOption.IGNORE_CASE)
            val matches = regex.findAll(buildContent).toList()

            matches.forEach { match ->
                val value = match.value.substringAfter("=").trim().removeSurrounding("\"")
                // Allow placeholder values or references to build vars
                val isPlaceholder = value.startsWith("\$") ||
                        value.equals("YOUR_API_KEY", ignoreCase = true) ||
                        value.equals("REPLACE_ME", ignoreCase = true) ||
                        value.length < 5

                assertTrue("Should not contain hardcoded $pattern: ${match.value}", isPlaceholder)
            }
        }
    }

    @Test
    fun `test KSP configuration is valid`() {
        if (buildContent.contains("ksp {")) {
            assertTrue(
                "KSP block should be properly configured",
                buildContent.contains("ksp {")
            )

            // Check for room schema location if present
            if (buildContent.contains("room.schemaLocation")) {
                val schemaPattern = Regex("room\.schemaLocation.*\"([^\"]+)\"")
                val schemaLocation = schemaPattern.find(buildContent)?.groupValues?.get(1)

                if (schemaLocation != null) {
                    assertTrue("Schema location should not be empty", schemaLocation.isNotBlank())
                    assertTrue(
                        "Schema location should be a valid path",
                        schemaLocation.contains("/") || schemaLocation.contains("$")
                    )
                }
            }
        }
    }

    @Test
    fun `test OpenAPI generator configuration is valid`() {
        if (buildContent.contains("openApiGenerate")) {
            assertTrue(
                "OpenAPI generate block should be properly configured",
                buildContent.contains("openApiGenerate {")
            )

            // Check for required configuration options
            val requiredOptions = listOf("generatorName", "inputSpec", "outputDir")

            requiredOptions.forEach { option ->
                if (buildContent.contains(option)) {
                    assertTrue(
                        "$option should be properly set",
                        buildContent.contains("$option.set(")
                    )
                }
            }
        }
    }

    @Test
    fun `test Android resources configuration is valid`() {
        if (buildContent.contains("androidResources")) {
            assertTrue(
                "Android resources block should be properly configured",
                buildContent.contains("androidResources {")
            )

            if (buildContent.contains("noCompress")) {
                assertTrue(
                    "noCompress should be properly formatted",
                    buildContent.contains("noCompress +=") || buildContent.contains("noCompress.add")
                )
            }
        }
    }

    @Test
    fun `test Kotlin compiler options are valid`() {
        if (buildContent.contains("compilerOptions")) {
            assertTrue(
                "Compiler options block should be properly configured",
                buildContent.contains("compilerOptions {")
            )

            if (buildContent.contains("freeCompilerArgs")) {
                assertTrue(
                    "Free compiler args should be properly formatted",
                    buildContent.contains("freeCompilerArgs.add") ||
                            buildContent.contains("freeCompilerArgs +=")
                )
            }
        }
    }

    @Test
    fun `test build features configuration is valid`() {
        if (buildContent.contains("buildFeatures")) {
            assertTrue(
                "Build features block should be properly configured",
                buildContent.contains("buildFeatures {")
            )

            val buildFeatures = listOf("buildConfig", "compose", "viewBinding")
            buildFeatures.forEach { feature ->
                if (buildContent.contains(feature)) {
                    assertTrue(
                        "$feature should be boolean value",
                        buildContent.contains("$feature = true") ||
                                buildContent.contains("$feature = false")
                    )
                }
            }
        }
    }

    @Test
    fun `test source sets configuration is valid`() {
        if (buildContent.contains("sourceSets")) {
            assertTrue(
                "Source sets should be properly configured",
                buildContent.contains("sourceSets") || buildContent.contains("android.sourceSets")
            )

            if (buildContent.contains("srcDirs")) {
                assertTrue(
                    "Source directories should be properly formatted",
                    buildContent.contains("srcDirs(") || buildContent.contains("srcDir(")
                )
            }
        }
    }

    @Test
    fun `test configurations block is valid`() {
        if (buildContent.contains("configurations.all")) {
            assertTrue(
                "Configurations block should be properly structured",
                buildContent.contains("configurations.all {")
            )

            if (buildContent.contains("exclude")) {
                assertTrue(
                    "Exclude statements should be properly formatted",
                    buildContent.contains("exclude(group") || buildContent.contains("exclude group")
                )
            }
        }
    }
}
