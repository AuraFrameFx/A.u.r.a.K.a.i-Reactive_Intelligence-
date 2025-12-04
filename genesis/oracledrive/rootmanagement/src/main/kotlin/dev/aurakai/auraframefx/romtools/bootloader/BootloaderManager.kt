// File: romtools/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManager.kt
package dev.aurakai.auraframefx.romtools.bootloader

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for bootloader management operations.
 */
interface BootloaderManager {
    /**
     * Checks if the device has bootloader access.
     * @return `true` if bootloader access is available, `false` otherwise.
     */
    fun checkBootloaderAccess(): Boolean

    /**
     * Checks if the bootloader is unlocked.
     * @return `true` if the bootloader is unlocked, `false` otherwise.
     */
    fun isBootloaderUnlocked(): Boolean

    /**
     * Unlocks the bootloader.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun unlockBootloader(): Result<Unit>
}

/**
 * Implementation of bootloader management with integrated safety checks.
 *
 * **SAFETY-FIRST ARCHITECTURE:**
 * This implementation integrates with BootloaderSafetyManager to ensure all operations
 * work WITH the system, not AGAINST it. Every operation undergoes pre-flight checks.
 *
 * **Key Features:**
 * 1. **Device-specific compatibility checks**: Validates device support before operations
 * 2. **Non-destructive verification**: Checks bootloader state without modifications
 * 3. **System integration**: Respects OEM unlock policies and SELinux state
 * 4. **Safety checkpoints**: Creates restore points before destructive operations
 * 5. **User guidance**: Provides step-by-step instructions for manual procedures
 *
 * **Implementation Status**: Safe read-only operations functional, write operations guided-only
 *
 * @see <a href="https://source.android.com/docs/core/architecture/bootloader">Android Bootloader Documentation</a>
 */
@Singleton
class BootloaderManagerImpl @Inject constructor(
    private val safetyManager: BootloaderSafetyManager
) : BootloaderManager {
    override fun checkBootloaderAccess(): Boolean {
        return try {
            // Perform safety checks first to ensure we can safely query bootloader state
            val safetyStatus = safetyManager.safetyStatus.value

            // Check if we can read bootloader-related system properties via getprop
            // This indicates the device exposes bootloader information
            val flashLocked = executeGetProp("ro.boot.flash.locked")
            val oemUnlock = executeGetProp("ro.oem_unlock_supported")
            val verifiedBoot = executeGetProp("ro.boot.verifiedbootstate")

            // If any property exists, bootloader access is available
            // Also check if device is compatible according to safety manager
            (flashLocked != null || oemUnlock != null || verifiedBoot != null) &&
            safetyStatus.deviceCompatible
        } catch (e: Exception) {
            // If we can't read properties, no bootloader access
            false
        }
    }

    override fun isBootloaderUnlocked(): Boolean {
        return try {
            // Read the bootloader lock status from system property via getprop
            // "0" = unlocked, "1" = locked, null = unknown
            val flashLocked = executeGetProp("ro.boot.flash.locked")

            when (flashLocked) {
                "0" -> true  // Bootloader is unlocked
                "1" -> false // Bootloader is locked
                else -> {
                    // Property doesn't exist or has unexpected value
                    // Check alternative property as fallback
                    val verified = executeGetProp("ro.boot.verifiedbootstate")
                    verified == "orange" // Orange state indicates unlocked bootloader
                }
            }
        } catch (e: Exception) {
            // Default to false (locked) if we can't determine status
            false
        }
    }

    override suspend fun unlockBootloader(): Result<Unit> {
        // 🔐 SAFETY-FIRST APPROACH: Perform comprehensive pre-flight checks
        val safetyCheck = safetyManager.performPreFlightChecks(BootloaderOperation.UNLOCK)

        if (!safetyCheck.passed) {
            // Critical issues detected - operation cannot proceed
            val errorMessage = buildString {
                append("❌ Pre-flight safety checks FAILED. Cannot proceed:\n\n")
                safetyCheck.criticalIssues.forEachIndexed { index, issue ->
                    append("${index + 1}. $issue\n")
                }
                append("\n⚠️ Please resolve these issues before attempting bootloader unlock.")
            }
            return Result.failure(IllegalStateException(errorMessage))
        }

        if (safetyCheck.warnings.isNotEmpty()) {
            // Warnings present - user should be aware but can proceed
            val warningMessage = buildString {
                append("⚠️ Safety warnings detected:\n\n")
                safetyCheck.warnings.forEachIndexed { index, warning ->
                    append("${index + 1}. $warning\n")
                }
            }
            // In a real implementation, you'd show this to the user via UI
            println(warningMessage)
        }

        // ⚠️ GUIDED APPROACH: Provide step-by-step instructions
        // Bootloader unlocking is intentionally NOT automated to prevent:
        // 1. Accidental data loss (bootloader unlock wipes all data)
        // 2. Warranty voiding without user knowledge
        // 3. Device bricking due to interrupted operations
        // 4. Security bypass attempts
        //
        // Instead, we guide users through the manufacturer's official process

        val guidedInstructions = buildString {
            append("🔓 BOOTLOADER UNLOCK GUIDE\n\n")
            append("IMPORTANT: Bootloader unlocking will ERASE ALL DATA on your device!\n\n")
            append("✅ Pre-flight checks passed. Your device is ready for unlock.\n\n")
            append("📋 OFFICIAL UNLOCK PROCEDURE:\n\n")
            append("1. ✅ ALREADY DONE: OEM Unlock is enabled in Developer Options\n")
            append("2. Backup all important data (THIS STEP WILL WIPE EVERYTHING!)\n")
            append("3. Obtain unlock code from your device manufacturer:\n")
            append("   - OnePlus: https://www.oneplus.com/unlock\n")
            append("   - Xiaomi: https://en.miui.com/unlock/\n")
            append("   - Google Pixel: No code needed, use fastboot\n")
            append("   - Samsung: Not officially supported by manufacturer\n")
            append("4. Reboot to bootloader mode:\n")
            append("   - Power off device\n")
            append("   - Hold Volume Down + Power until bootloader menu appears\n")
            append("5. Connect device to PC with USB cable\n")
            append("6. Run command: fastboot flashing unlock\n")
            append("7. Confirm on device screen (⚠️ THIS WIPES ALL DATA!)\n")
            append("8. Device will reboot with unlocked bootloader\n\n")
            append("🔐 SAFETY REMINDER:\n")
            append("- This operation is irreversible without re-locking (which also wipes data)\n")
            append("- Your device will show 'Bootloader unlocked' warning on every boot\n")
            append("- Some banking/payment apps may not work with unlocked bootloader\n")
            append("- Device warranty may be voided (check manufacturer policy)\n\n")
            append("For automated unlock assistance, use Genesis ROM Tools > Bootloader Manager UI")
        }

        return Result.failure(
            UnsupportedOperationException(guidedInstructions)
        )
    }

    /**
     * Executes getprop command to read Android system property.
     * @param property The system property name to read
     * @return The property value, or null if the property doesn't exist or can't be read
     */
    private fun executeGetProp(property: String): String? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", property))
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            process.waitFor()

            // Return null if the output is empty (property doesn't exist)
            output.ifEmpty { null }
        } catch (e: Exception) {
            // Return null if we can't execute getprop
            null
        }
    }
}
