package dev.aurakai.auraframefx.gradle

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Tests for OpenAPI generator configuration validation
 *
 * Testing Framework: JUnit 4
 */
class OpenApiConfigurationTest {

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
    fun `test OpenAPI spec file path is configured`() {
        assertTrue(
            "OpenAPI spec path should be configured",
            buildContent.contains("src/main/openapi.yml")
        )
    }

    @Test
    fun `test OpenAPI generator output directory is correct`() {
        assertTrue(
            "Output directory should be in build/generated",
            buildContent.contains("layout.buildDirectory.get().asFile}/generated/kotlin")
        )
    }

    @Test
    fun `test OpenAPI package names follow conventions`() {
        assertTrue(
            "API package should follow naming convention",
            buildContent.contains("dev.aurakai.auraframefx.api.client.apis")
        )
        assertTrue(
            "Model package should follow naming convention",
            buildContent.contains("dev.aurakai.auraframefx.api.client.models")
        )
        assertTrue(
            "Infrastructure package should follow naming convention",
            buildContent.contains("dev.aurakai.auraframefx.api.client.infrastructure")
        )
    }

    @Test
    fun `test OpenAPI generator options are appropriate`() {
        assertTrue(
            "Should use Java 8 date library",
            buildContent.contains("\"dateLibrary\" to \"java8\"")
        )
        assertTrue(
            "Should enable coroutines",
            buildContent.contains("\"useCoroutines\" to \"true\"")
        )
        assertTrue(
            "Should use list collection type",
            buildContent.contains("\"collectionType\" to \"list\"")
        )
    }

    @Test
    fun `test OpenAPI generation task dependency`() {
        assertTrue(
            "PreBuild should depend on OpenAPI generation",
            buildContent.contains("dependsOn(\"openApiGenerate\")")
        )
    }

    @Test
    fun `test source sets include generated code`() {
        assertTrue(
            "Generated Kotlin sources should be included",
            buildContent.contains("srcDirs(\"\${layout.buildDirectory.get()}/generated/kotlin\")")
        )
    }

    @Test
    fun `test OpenAPI generator is properly configured`() {
        assertTrue(
            "OpenAPI generator name should be kotlin",
            buildContent.contains("generatorName.set(\"kotlin\")")
        )

        // Test input spec configuration
        assertTrue(
            "Input spec should be configured",
            buildContent.contains("inputSpec.set(openApiSpecPath)")
        )

        // Test output directory configuration
        assertTrue(
            "Output directory should be configured",
            buildContent.contains("outputDir.set(")
        )
    }

    @Test
    fun `test OpenAPI path handling for cross-platform compatibility`() {
        assertTrue(
            "Should handle Windows paths correctly",
            buildContent.contains("file(\"src/main/openapi.yml\").toURI().toURL().toString()")
        )
    }

    @Test
    fun `test OpenAPI configuration options are comprehensive`() {
        val requiredOptions = listOf(
            "dateLibrary",
            "useCoroutines",
            "collectionType"
        )

        requiredOptions.forEach { option ->
            assertTrue(
                "OpenAPI should configure $option",
                buildContent.contains("\"$option\"")
            )
        }
    }
}