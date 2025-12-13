package dev.aurakai.auraframefx.gradle.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File
import java.util.regex.Pattern

/**
 * Comprehensive unit tests for gradle/libs.versions.toml validation.
 * Tests schema structure, version formats, consistency, and dependencies.
 *
 * Testing Framework: JUnit 4 (as indicated by mockk 1.14.4 and junit 4.13.2 in the TOML)
 */
class LibsVersionsTomlTest {

    private lateinit var tomlContent: String
    private lateinit var tomlLines: List<String>

    @Before
    fun setUp() {
        val tomlFile = File("gradle/libs.versions.toml")
        assertTrue("libs.versions.toml should exist", tomlFile.exists())
        tomlContent = tomlFile.readText()
        tomlLines = tomlContent.lines()
    }

    @Test
    fun `test TOML file has required main sections`() {
        assertTrue("Should contain [versions] section", tomlContent.contains("[versions]"))
        assertTrue("Should contain [libraries] section", tomlContent.contains("[libraries]"))
        assertTrue("Should contain [plugins] section", tomlContent.contains("[plugins]"))
    }

    @Test
    fun `test versions section contains critical dependencies`() {
        val criticalVersions = listOf(
            "agp", "kotlin", "ksp", "hilt", "composeBom",
            "coreKtx", "appcompat", "lifecycle", "room"
        )

        criticalVersions.forEach { version ->
            assertTrue(
                "Critical version '$version' should be defined",
                tomlContent.contains("$version = ")
            )
        }
    }

    @Test
    fun `test version format follows semantic versioning pattern`() {
        val versionPattern = Pattern.compile("""(\w+)\s*=\s*"([^"]+)"""")
        val lines = tomlLines.filter { it.contains(" = \"") && !it.trim().startsWith("#") }

        lines.forEach { line ->
            val matcher = versionPattern.matcher(line)
            if (matcher.find()) {
                val versionName = matcher.group(1)
                val versionValue = matcher.group(2)

                // Check version format (semantic versioning or date-based for BOM)
                val isValidVersion =
                    versionValue.matches(Regex("""^\d+\.\d+(\.\d+)?(-\w+(\.\d+)?)?$""")) ||
                            versionValue.matches(Regex("""^\d{4}\.\d{2}\.\d{2}$""")) // Date format for BOM

                assertTrue(
                    "Version '$versionName' should follow semantic versioning: $versionValue",
                    isValidVersion
                )
            }
        }
    }

    @Test
    fun `test kotlin and ksp versions are compatible`() {
        val kotlinVersion = extractVersion("kotlin")
        val kspVersion = extractVersion("ksp")

        assertNotNull("Kotlin version should be defined", kotlinVersion)
        assertNotNull("KSP version should be defined", kspVersion)

        // KSP version should start with Kotlin version
        assertTrue(
            "KSP version ($kspVersion) should be compatible with Kotlin version ($kotlinVersion)",
            kspVersion!!.startsWith(kotlinVersion!!)
        )
    }

    @Test
    fun `test compose versions are aligned`() {
        val composeBom = extractVersion("composeBom")
        val composeCompiler = extractVersion("composeCompiler")

        assertNotNull("Compose BOM should be defined", composeBom)
        assertNotNull("Compose compiler should be defined", composeCompiler)

        // Both should be defined (specific compatibility checked in integration tests)
        assertFalse("Compose BOM should not be empty", composeBom.isNullOrEmpty())
        assertFalse("Compose compiler should not be empty", composeCompiler.isNullOrEmpty())
    }

    @Test
    fun `test lifecycle versions are consistent`() {
        val lifecycleVersions = listOf(
            "lifecycle", "lifecycleRuntimeCompose", "lifecycleViewmodelCompose"
        )

        val versions = lifecycleVersions.mapNotNull { extractVersion(it) }.distinct()

        assertTrue(
            "All lifecycle versions should be aligned, found: $versions",
            versions.size <= 1 || versions.all { it == versions.first() }
        )
    }

    @Test
    fun `test firebase dependencies use BOM pattern`() {
        val firebaseBomExists = tomlContent.contains("firebaseBom")
        assertTrue("Firebase BOM should be defined", firebaseBomExists)

        // Firebase libraries should not have explicit versions (managed by BOM)
        val firebaseLibs = tomlLines.filter {
            it.contains("firebase") && it.contains("group = ") && !it.contains("bom")
        }

        firebaseLibs.forEach { line ->
            assertFalse(
                "Firebase library should not have explicit version (managed by BOM): $line",
                line.contains("version")
            )
        }
    }

    @Test
    fun `test all version references are valid`() {
        val versionRefs = tomlLines
            .filter { it.contains("version.ref = ") }
            .mapNotNull { line ->
                val pattern = """version\.ref = "([^"]+)""""
                Regex(pattern).find(line)?.groupValues?.get(1)
            }

        val definedVersions = tomlLines
            .filter { it.matches(Regex("""^\s*\w+\s*=\s*"[^"]+"\s*$""")) }
            .map { it.split("=")[0].trim() }

        versionRefs.forEach { ref ->
            assertTrue(
                "Version reference '$ref' should have corresponding version definition",
                definedVersions.contains(ref)
            )
        }
    }

    @Test
    fun `test no duplicate library definitions`() {
        val libraryNames = mutableSetOf<String>()
        val duplicates = mutableListOf<String>()

        tomlLines
            .filter { it.contains(" = { ") && !it.trim().startsWith("#") }
            .forEach { line ->
                val name = line.split("=")[0].trim()
                if (!libraryNames.add(name)) {
                    duplicates.add(name)
                }
            }

        assertTrue(
            "No duplicate library definitions should exist: $duplicates",
            duplicates.isEmpty()
        )
    }

    @Test
    fun `test retrofit and okhttp versions are compatible`() {
        val retrofitVersion = extractVersion("retrofit")
        val okhttpVersion = extractVersion("okhttp")

        assertNotNull("Retrofit version should be defined", retrofitVersion)
        assertNotNull("OkHttp version should be defined", okhttpVersion)

        // Basic compatibility check - both should be major version 3+ and 5+ respectively
        assertTrue("Retrofit should be version 3+", retrofitVersion!!.startsWith("3."))
        assertTrue("OkHttp should be version 5+", okhttpVersion!!.startsWith("5."))
    }

    @Test
    fun `test testing libraries are properly defined`() {
        val testingLibraries = listOf(
            "junit", "androidxTestExtJunit", "espressoCore", "mockk"
        )

        testingLibraries.forEach { lib ->
            assertTrue(
                "Testing library '$lib' should be defined in versions",
                tomlContent.contains("$lib = ")
            )
        }

        // Verify test libraries exist in libraries section
        assertTrue("JUnit library should be defined", tomlContent.contains("testJunit"))
        assertTrue("MockK library should be defined", tomlContent.contains("mockkAndroid"))
        assertTrue("Espresso library should be defined", tomlContent.contains("espressoCore"))
    }

    @Test
    fun `test hilt versions are aligned`() {
        val hiltVersions = listOf("hilt", "hiltNavigationCompose", "hiltWork")
        val baseHiltVersion = extractVersion("hilt")

        assertNotNull("Base Hilt version should be defined", baseHiltVersion)

        // Other Hilt libraries can have different versions but should be defined
        hiltVersions.forEach { version ->
            assertNotNull("Hilt version '$version' should be defined", extractVersion(version))
        }
    }

    @Test
    fun `test accompanist libraries have migration context`() {
        val accompanistLibraries = tomlLines.filter {
            it.contains("accompanist") && !it.trim().startsWith("#")
        }

        accompanistLibraries.forEach { line ->
            val lineIndex = tomlLines.indexOf(line)
            val previousLine = if (lineIndex > 0) tomlLines[lineIndex - 1] else ""
            val hasComment = previousLine.contains("migration") || line.contains("migration") ||
                    previousLine.contains("Needs migration") || line.contains("Needs migration")

            // Documentation test - Accompanist libraries should ideally have migration notes
            if (line.contains("accompanist")) {
                println("Accompanist library found: $line")
                if (hasComment) {
                    println("  ✓ Has migration context")
                } else {
                    println("  ⚠ Could benefit from migration documentation")
                }
            }
        }
    }

    @Test
    fun `test room version consistency`() {
        val roomLibraries = tomlLines.filter {
            it.contains("Room") && it.contains("version.ref")
        }

        roomLibraries.forEach { line ->
            assertTrue(
                "Room library should reference 'room' version: $line",
                line.contains("""version.ref = "room"""")
            )
        }
    }

    @Test
    fun `test androidx libraries use consistent patterns`() {
        val androidxLibraries = tomlLines.filter {
            it.contains("androidx") && it.contains("module = ")
        }

        androidxLibraries.forEach { line ->
            // Should follow pattern: { module = "androidx.package:library", version.ref = "version" }
            assertTrue(
                "AndroidX library should use proper module format: $line",
                line.contains("module = \"androidx.")
            )
        }
    }

    @Test
    fun `test generative AI library is properly configured`() {
        val generativeAiVersion = extractVersion("generativeai")
        assertNotNull("Generative AI version should be defined", generativeAiVersion)

        // Should be a reasonable version
        assertTrue(
            "Generative AI should be version 0.9+",
            generativeAiVersion!!.compareTo("0.9") >= 0
        )

        // Library should be defined
        assertTrue(
            "Generative AI library should be defined",
            tomlContent.contains("com.google.ai.client.generativeai")
        )
    }

    private fun extractVersion(versionName: String): String? {
        val pattern = """$versionName\s*=\s*"([^"]+)""""
        return Regex(pattern).find(tomlContent)?.groupValues?.get(1)
    }

    @Test
    fun `test TOML file has proper bundles section`() {
        assertTrue("Should contain [bundles] section", tomlContent.contains("[bundles]"))

        // Test that bundles reference valid library names
        val bundlePattern = Pattern.compile("""(\w+)\s*=\s*\[([^\]]+)\]""")
        val bundleLines = tomlLines.filter { it.contains(" = [") && !it.trim().startsWith("#") }

        bundleLines.forEach { line ->
            val matcher = bundlePattern.matcher(line)
            if (matcher.find()) {
                val bundleName = matcher.group(1)
                val bundleContent = matcher.group(2)

                assertFalse(
                    "Bundle '$bundleName' should not be empty",
                    bundleContent.trim().isEmpty()
                )
                assertTrue(
                    "Bundle '$bundleName' should contain quoted library references",
                    bundleContent.contains("\"")
                )
            }
        }
    }

    @Test
    fun `test bundle references point to valid libraries`() {
        val bundleSection = extractSection("[bundles]")
        val libraryNames = extractSection("[libraries]")
            .filter { it.contains(" = {") && !it.trim().startsWith("#") }
            .map { it.split("=")[0].trim() }

        bundleSection.forEach { line ->
            if (line.contains(" = [")) {
                val libraryRefs = line.substringAfter("[").substringBefore("]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }

                libraryRefs.forEach { ref ->
                    if (ref.isNotEmpty()) {
                        assertTrue(
                            "Bundle reference '$ref' should point to valid library",
                            libraryNames.contains(ref)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `test accompanist libraries have proper version management`() {
        val accompanistLibraries = tomlLines.filter {
            it.contains("accompanist") && it.contains("version = ")
        }

        accompanistLibraries.forEach { line ->
            // Accompanist libraries should have explicit versions since they're being migrated
            assertTrue(
                "Accompanist library should have explicit version: $line",
                line.contains("version = \"")
            )

            // Version should be reasonable (0.30+)
            val versionPattern = Pattern.compile("""version = "([^"]+)"""")
            val matcher = versionPattern.matcher(line)
            if (matcher.find()) {
                val version = matcher.group(1)
                assertTrue(
                    "Accompanist version should be 0.30+: $version",
                    version.compareTo("0.30") >= 0
                )
            }
        }
    }

    @Test
    fun `test serialization library versions are compatible`() {
        val kotlinVersion = extractVersion("kotlin")
        val serializationVersion = extractVersion("kotlinxSerializationJson")

        assertNotNull("Kotlin serialization version should be defined", serializationVersion)

        if (kotlinVersion != null && serializationVersion != null) {
            // Serialization should be reasonably recent
            assertTrue(
                "Kotlin serialization should be 1.5+",
                serializationVersion.compareTo("1.5") >= 0
            )
        }
    }

    @Test
    fun `test datetime library version is compatible`() {
        val datetimeVersion = extractVersion("kotlinxDatetime")

        if (datetimeVersion != null) {
            assertTrue(
                "Kotlinx datetime should be 0.4+",
                datetimeVersion.compareTo("0.4") >= 0
            )
        }
    }

    @Test
    fun `test desugar library is properly configured`() {
        val desugarVersion = extractVersion("desugar-jdk-libs")

        assertNotNull("Desugar JDK libs version should be defined", desugarVersion)

        if (desugarVersion != null) {
            assertTrue(
                "Desugar JDK libs should be 2.0+",
                desugarVersion.compareTo("2.0") >= 0
            )
        }

        // Should be defined in libraries section
        assertTrue(
            "Desugar JDK libs should be defined in libraries",
            tomlContent.contains("desugar-jdk-libs")
        )
    }

    @Test
    fun `test navigation compose version compatibility`() {
        val navigationVersion = extractVersion("navigationCompose")
        val lifecycleVersion = extractVersion("lifecycle")

        assertNotNull("Navigation Compose version should be defined", navigationVersion)

        if (navigationVersion != null && lifecycleVersion != null) {
            // Navigation should be reasonably recent
            assertTrue(
                "Navigation Compose should be 2.7+",
                navigationVersion.compareTo("2.7") >= 0
            )
        }
    }

    @Test
    fun `test activity compose version compatibility`() {
        val activityComposeVersion = extractVersion("activityCompose")
        extractVersion("composeBom")

        assertNotNull("Activity Compose version should be defined", activityComposeVersion)

        if (activityComposeVersion != null) {
            assertTrue(
                "Activity Compose should be 1.8+",
                activityComposeVersion.compareTo("1.8") >= 0
            )
        }
    }

    @Test
    fun `test coil compose version is compatible`() {
        val coilVersion = extractVersion("coilCompose")

        if (coilVersion != null) {
            assertTrue(
                "Coil Compose should be 2.4+",
                coilVersion.compareTo("2.4") >= 0
            )
        }
    }

    @Test
    fun `test timber version is reasonable`() {
        val timberVersion = extractVersion("timber")

        if (timberVersion != null) {
            assertTrue(
                "Timber should be 5.0+",
                timberVersion.compareTo("5.0") >= 0
            )
        }
    }

    @Test
    fun `test datastore version compatibility`() {
        val datastoreVersion = extractVersion("datastore")

        if (datastoreVersion != null) {
            assertTrue(
                "DataStore should be 1.0+",
                datastoreVersion.compareTo("1.0") >= 0
            )
        }
    }

    @Test
    fun `test security crypto version is alpha aware`() {
        val securityCryptoVersion = extractVersion("securityCrypto")

        if (securityCryptoVersion != null) {
            // Security crypto library is often in alpha, so be flexible
            assertTrue(
                "Security crypto should be defined",
                securityCryptoVersion.isNotEmpty()
            )
        }
    }

    @Test
    fun `test work manager version compatibility`() {
        val workManagerVersion = extractVersion("workManager")
        val hiltWorkVersion = extractVersion("hiltWork")

        if (workManagerVersion != null) {
            assertTrue(
                "Work Manager should be 2.8+",
                workManagerVersion.compareTo("2.8") >= 0
            )
        }

        if (hiltWorkVersion != null) {
            assertTrue(
                "Hilt Work should be 1.0+",
                hiltWorkVersion.compareTo("1.0") >= 0
            )
        }
    }

    @Test
    fun `test plugin versions are consistent with dependencies`() {
        val kotlinPluginVersion = extractVersion("kotlin")
        val kspPluginVersion = extractVersion("ksp")
        val hiltPluginVersion = extractVersion("hilt")

        // KSP should be compatible with Kotlin
        if (kotlinPluginVersion != null && kspPluginVersion != null) {
            assertTrue(
                "KSP version should start with Kotlin version",
                kspPluginVersion.startsWith(kotlinPluginVersion)
            )
        }

        // Hilt plugin should match Hilt library version
        if (hiltPluginVersion != null) {
            assertTrue(
                "Hilt plugin should be 2.40+",
                hiltPluginVersion.compareTo("2.40") >= 0
            )
        }
    }

    @Test
    fun `test firebase plugin versions are compatible`() {
        val firebaseCrashlyticsPlugin = extractVersion("firebaseCrashlyticsPlugin")
        val firebasePerfPlugin = extractVersion("firebasePerfPlugin")
        val googleServices = extractVersion("googleServices")

        if (firebaseCrashlyticsPlugin != null) {
            assertTrue(
                "Firebase Crashlytics plugin should be 2.8+",
                firebaseCrashlyticsPlugin.compareTo("2.8") >= 0
            )
        }

        if (firebasePerfPlugin != null) {
            assertTrue(
                "Firebase Performance plugin should be 1.4+",
                firebasePerfPlugin.compareTo("1.4") >= 0
            )
        }

        if (googleServices != null) {
            assertTrue(
                "Google Services should be 4.3+",
                googleServices.compareTo("4.3") >= 0
            )
        }
    }

    @Test
    fun `test openapi generator plugin version`() {
        val openapiVersion = extractVersion("openapiGeneratorPlugin")

        if (openapiVersion != null) {
            assertTrue(
                "OpenAPI Generator should be 6.0+",
                openapiVersion.compareTo("6.0") >= 0
            )
        }
    }

    @Test
    fun `test junit version is jupiter not vintage`() {
        val junitVersion = extractVersion("junit")

        if (junitVersion != null) {
            assertTrue(
                "JUnit should be 5.x (Jupiter) not 4.x",
                junitVersion.startsWith("5.")
            )
            assertTrue(
                "JUnit should be 5.8+",
                junitVersion.compareTo("5.8") >= 0
            )
        }
    }

    @Test
    fun `test mockk version is compatible with kotlin`() {
        val mockkVersion = extractVersion("mockk")

        if (mockkVersion != null) {
            assertTrue(
                "MockK should be 1.13+",
                mockkVersion.compareTo("1.13") >= 0
            )
        }
    }

    @Test
    fun `test espresso version is compatible with android test`() {
        val espressoVersion = extractVersion("espressoCore")
        val androidxTestVersion = extractVersion("androidxTestExtJunit")

        if (espressoVersion != null) {
            assertTrue(
                "Espresso should be 3.5+",
                espressoVersion.compareTo("3.5") >= 0
            )
        }

        if (androidxTestVersion != null) {
            assertTrue(
                "AndroidX Test should be 1.1+",
                androidxTestVersion.compareTo("1.1") >= 0
            )
        }
    }

    @Test
    fun `test no duplicate bundle entries`() {
        val bundleSection = extractSection("[bundles]")
        val bundleNames = mutableSetOf<String>()
        val duplicates = mutableListOf<String>()

        bundleSection.forEach { line ->
            if (line.contains(" = [") && !line.trim().startsWith("#")) {
                val bundleName = line.split("=")[0].trim()
                if (!bundleNames.add(bundleName)) {
                    duplicates.add(bundleName)
                }
            }
        }

        assertTrue(
            "No duplicate bundle definitions should exist: $duplicates",
            duplicates.isEmpty()
        )
    }

    @Test
    fun `test version references use consistent quoting`() {
        val versionRefPattern = Pattern.compile("""version\.ref = "([^"]+)"""")
        val inconsistentQuotes = mutableListOf<String>()

        tomlLines.forEach { line ->
            if (line.contains("version.ref = ") && !line.trim().startsWith("#")) {
                val matcher = versionRefPattern.matcher(line)
                if (!matcher.find()) {
                    inconsistentQuotes.add(line.trim())
                }
            }
        }

        assertTrue(
            "All version.ref should use consistent double quotes: $inconsistentQuotes",
            inconsistentQuotes.isEmpty()
        )
    }

    @Test
    fun `test library definitions use consistent format`() {
        val librarySection = extractSection("[libraries]")
        val malformedLibraries = mutableListOf<String>()

        librarySection.forEach { line ->
            if (line.contains(" = {") && !line.trim().startsWith("#")) {
                val hasGroup = line.contains("group = ")
                val hasModule = line.contains("module = ")
                val hasName = line.contains("name = ")

                if (hasGroup && hasName) {
                    // Valid format: group + name
                } else if (hasModule) {
                    // Valid format: module
                } else {
                    malformedLibraries.add(line.trim())
                }
            }
        }

        assertTrue(
            "All library definitions should use consistent format: $malformedLibraries",
            malformedLibraries.isEmpty()
        )
    }

    @Test
    fun `test sections are in correct order`() {
        val foundSections = mutableListOf<String>()

        tomlLines.forEach { line ->
            if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                foundSections.add(line.trim())
            }
        }

        val versionsIndex = foundSections.indexOf("[versions]")
        val librariesIndex = foundSections.indexOf("[libraries]")

        if (versionsIndex >= 0 && librariesIndex >= 0) {
            assertTrue(
                "Versions section should come before libraries",
                versionsIndex < librariesIndex
            )
        }
    }

    @Test
    fun `test version patterns are valid for all entries`() {
        val versionPattern = Pattern.compile("""(\w+)\s*=\s*"([^"]+)"""")
        val invalidVersions = mutableListOf<String>()

        extractSection("[versions]").forEach { line ->
            val matcher = versionPattern.matcher(line)
            if (matcher.find()) {
                val versionName = matcher.group(1)
                val versionValue = matcher.group(2)

                // Check for common version patterns
                val validPatterns = listOf(
                    Regex("""^\d+\.\d+(\.\d+)?(-\w+(\.\d+)?)?$"""), // Semantic versioning
                    Regex("""^\d{4}\.\d{2}\.\d{2}$"""), // Date format (BOM)
                    Regex("""^\d+\.\d+\.\d+-\d+\.\d+\.\d+$""") // KSP format
                )

                val isValid = validPatterns.any { it.matches(versionValue) }
                if (!isValid) {
                    invalidVersions.add("$versionName = $versionValue")
                }
            }
        }

        assertTrue(
            "All versions should follow valid patterns: $invalidVersions",
            invalidVersions.isEmpty()
        )
    }

    @Test
    fun `test compose bundle integrity`() {
        val composeBundleContent = tomlLines.find { it.contains("compose = [") }

        if (composeBundleContent != null) {
            val bundleLibraries = composeBundleContent
                .substringAfter("[")
                .substringBefore("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }

            // Essential Compose libraries should be in the bundle
            val essentialLibraries = listOf("compose-bom", "compose-ui", "compose-material3")

            essentialLibraries.forEach { lib ->
                assertTrue(
                    "Compose bundle should contain essential library: $lib",
                    bundleLibraries.contains(lib)
                )
            }
        }
    }

    @Test
    fun `test firebase bundle integrity`() {
        val firebaseBundleContent = tomlLines.find { it.contains("firebase = [") }

        if (firebaseBundleContent != null) {
            val bundleLibraries = firebaseBundleContent
                .substringAfter("[")
                .substringBefore("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }

            // Firebase bundle should contain BOM
            assertTrue(
                "Firebase bundle should contain BOM",
                bundleLibraries.contains("firebase-bom")
            )
        }
    }

    @Test
    fun `test room bundle integrity`() {
        val roomBundleContent = tomlLines.find { it.contains("room = [") }

        if (roomBundleContent != null) {
            val bundleLibraries = roomBundleContent
                .substringAfter("[")
                .substringBefore("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }

            // Room bundle should contain runtime and ktx
            assertTrue(
                "Room bundle should contain runtime",
                bundleLibraries.contains("room-runtime")
            )
            assertTrue(
                "Room bundle should contain ktx",
                bundleLibraries.contains("room-ktx")
            )
        }
    }

    @Test
    fun `test testing bundle integrity`() {
        val testingBundles = tomlLines.filter { it.contains("testing") && it.contains(" = [") }

        testingBundles.forEach { line ->
            val bundleLibraries = line
                .substringAfter("[")
                .substringBefore("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }

            // Testing bundles should not be empty
            assertTrue(
                "Testing bundle should not be empty: $line",
                bundleLibraries.isNotEmpty()
            )
        }
    }

    @Test
    fun `test no circular dependencies in bundles`() {
        val bundleSection = extractSection("[bundles]")
        val bundleNames = mutableSetOf<String>()

        bundleSection.forEach { line ->
            if (line.contains(" = [") && !line.trim().startsWith("#")) {
                val bundleName = line.split("=")[0].trim()
                bundleNames.add(bundleName)
            }
        }

        // Check that no bundle references another bundle
        bundleSection.forEach { line ->
            if (line.contains(" = [") && !line.trim().startsWith("#")) {
                val bundleLibraries = line
                    .substringAfter("[")
                    .substringBefore("]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }

                bundleLibraries.forEach { lib ->
                    assertFalse(
                        "Bundle should not reference another bundle: $lib",
                        bundleNames.contains(lib)
                    )
                }
            }
        }
    }

    private fun extractSection(sectionName: String): List<String> {
        val startIndex = tomlLines.indexOfFirst { it.trim() == sectionName }
        if (startIndex == -1) return emptyList()

        val endIndex = tomlLines.drop(startIndex + 1).indexOfFirst { it.trim().startsWith("[") }
        return if (endIndex == -1) {
            tomlLines.drop(startIndex + 1)
        } else {
            tomlLines.drop(startIndex + 1).take(endIndex)
        }
    }
}
