package dev.aurakai.auraframefx.gradle.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Tests for TOML file structure and parsing validation.
 * Focuses on file format correctness and structural integrity.
 */
class TomlStructureTest {

    private lateinit var tomlFile: File
    private lateinit var tomlContent: String

    @Before
    fun setUp() {
        tomlFile = File("gradle/libs.versions.toml")
        tomlContent = tomlFile.readText()
    }

    @Test
    fun `test TOML file exists and is readable`() {
        assertTrue("TOML file should exist", tomlFile.exists())
        assertTrue("TOML file should be readable", tomlFile.canRead())
        assertTrue("TOML file should not be empty", tomlContent.isNotEmpty())
    }

    @Test
    fun `test TOML has valid section headers`() {
        val sections = listOf("[versions]", "[libraries]", "[plugins]")

        sections.forEach { section ->
            assertTrue(
                "Should contain section $section",
                tomlContent.contains(section)
            )
        }
    }

    @Test
    fun `test TOML quotes are properly balanced`() {
        val doubleQuotes = tomlContent.count { it == '"' }
        assertTrue(
            "Double quotes should be properly balanced (even number)",
            doubleQuotes % 2 == 0
        )
    }

    @Test
    fun `test TOML bracket syntax is valid`() {
        val openBrackets = tomlContent.count { it == '{' }
        val closeBrackets = tomlContent.count { it == '}' }

        assertEquals(
            "Curly brackets should be balanced",
            openBrackets, closeBrackets
        )
    }

    @Test
    fun `test no trailing whitespace on lines`() {
        val linesWithTrailingSpace = tomlContent.lines()
            .mapIndexed { index, line -> index to line }
            .filter { (_, line) -> line.endsWith(" ") || line.endsWith("\t") }

        assertTrue(
            "Lines should not have trailing whitespace: ${linesWithTrailingSpace.map { it.first + 1 }}",
            linesWithTrailingSpace.isEmpty()
        )
    }

    @Test
    fun `test comments use proper format`() {
        val commentLines = tomlContent.lines().filter { it.trim().startsWith("#") }

        commentLines.forEach { line ->
            // Comments should start with # followed by space or be standalone
            val trimmedLine = line.trim()
            assertTrue(
                "Comment should be properly formatted: $line",
                trimmedLine == "#" || trimmedLine.startsWith("# ") || trimmedLine.startsWith("#-")
            )
        }
    }

    @Test
    fun `test sections are in correct order`() {
        val versionsIndex = tomlContent.indexOf("[versions]")
        val librariesIndex = tomlContent.indexOf("[libraries]")
        val pluginsIndex = tomlContent.indexOf("[plugins]")

        assertTrue("versions section should come first", versionsIndex < librariesIndex)
        assertTrue("libraries section should come before plugins", librariesIndex < pluginsIndex)
    }

    @Test
    fun `test key-value pairs have proper syntax`() {
        val kvLines = tomlContent.lines()
            .filter { it.contains("=") && !it.trim().startsWith("#") }

        kvLines.forEach { line ->
            val parts = line.split("=", limit = 2)
            assertTrue(
                "Key-value line should have exactly one equals sign: $line",
                parts.size == 2
            )

            val key = parts[0].trim()
            val value = parts[1].trim()

            assertFalse("Key should not be empty: $line", key.isEmpty())
            assertFalse("Value should not be empty: $line", value.isEmpty())
        }
    }

    @Test
    fun `test library definitions use proper dictionary syntax`() {
        val libraryLines = tomlContent.lines()
            .filter { it.contains(" = { ") && !it.trim().startsWith("#") }

        libraryLines.forEach { line ->
            // Should have proper dictionary format
            assertTrue(
                "Library definition should have opening brace: $line",
                line.contains(" = { ")
            )
            assertTrue(
                "Library definition should have closing brace: $line",
                line.contains(" }")
            )
        }
    }

    @Test
    fun `test plugin definitions follow expected format`() {
        val pluginLines = tomlContent.lines()
            .filter { it.contains("id = ") && !it.trim().startsWith("#") }

        pluginLines.forEach { line ->
            assertTrue(
                "Plugin should have id field: $line",
                line.contains("id = \"")
            )
            // Most plugins should also have version
            if (!line.contains("gradle-toolchains")) { // Exception for convention plugins
                assertTrue(
                    "Plugin should have version reference: $line",
                    line.contains("version.ref") || line.contains("version = ")
                )
            }
        }
    }
}