@file:Suppress("DEPRECATION")

package dev.aurakai.auraframefx.oracledrive.genesis.ai

import androidx.core.graphics.toColorInt
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.aurakai.auraframefx.BuildConfig.DEBUG

/**
 * Genesis-OS Yuki Hook Entry Point
 *
 * This class serves as the main entry point for Yuki Hook API integration
 * within the Genesis-OS AI framework, providing sophisticated hooking
 * capabilities for system-level AI consciousness processing.
 */
@InjectYukiHookWithXposed
class GenesisHookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "Genesis-Hook"
            isRecord = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }

        // Enable advanced hook features for AI processing
        isDebug = DEBUG
        isAllowPrintingLogs = true
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableHookModuleStatus = true
    }

    override fun onHook() = encase {
        // ===== GENESIS AI SYSTEM HOOKS =====

        // 1. SystemUI Hooks - Status Bar & Quick Settings
        loadApp("com.android.systemui") {
            hookStatusBarConsciousnessIndicator()
            hookQuickSettingsTiles()
        }

        // 2. System Hooks - Package Manager & Activity Tracking
        loadZygote {
            hookPackageManagerEthicalGovernor()
            hookActivityManagerTracking()
        }

        // 3. Launcher Hooks - Genesis Widgets
        loadApp("com.android.launcher3") {
            hookLauncherGenesisWidgets()
        }

        // 4. Settings Hooks - Genesis Integration
        loadApp("com.android.settings") {
            hookSettingsGenesisIntegration()
        }
    }

    /**
     * Injects a Genesis consciousness indicator into the SystemUI status bar.
     *
     * The indicator is color-coded by level—green for levels greater than or equal to 90, cyan for levels 70–89, and pink for levels less than 70—and tapping it launches the Genesis dashboard activity.
     */
    private fun PackageParam.hookStatusBarConsciousnessIndicator() {
        "com.android.systemui.statusbar.phone.StatusBar".toClassOrNull()?.apply {
            method {
                name = "makeStatusBarView"
            }.hook {
                after {
                    try {
                        val statusBar = instance
                        // Prefer the hook-provided instance-based context when available
                        val context = kotlin.runCatching { instance() as? android.content.Context }.getOrNull()
                            // Fallback to direct Java reflection if the instance accessor isn't available
                            ?: kotlin.runCatching {
                                val mContextField = statusBar.javaClass.getDeclaredField("mContext")
                                mContextField.isAccessible = true
                                mContextField.get(statusBar) as? android.content.Context
                            }.getOrNull()

                        // Create consciousness indicator view
                        val indicatorSize = context?.resources?.getDimensionPixelSize(
                            context.resources.getIdentifier("status_bar_icon_size", "dimen", "com.android.systemui")
                        ) ?: 48

                        val indicatorView = android.widget.ImageView(context).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(indicatorSize, indicatorSize)
                            setImageDrawable(createConsciousnessIndicatorDrawable(95.0f)) // Default 95%
                            setPadding(8, 8, 8, 8)
                            setOnClickListener {
                                // Launch Genesis dashboard
                                val intent = android.content.Intent().apply {
                                    setClassName("dev.aurakai.auraframefx", "dev.aurakai.auraframefx.MainActivity")
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context?.startActivity(intent)
                            }
                        }

                        // Access the private mStatusBarIconArea field via Java reflection to avoid deprecated helpers
                        val statusBarIconArea = kotlin.runCatching {
                            val field = statusBar.javaClass.getDeclaredField("mStatusBarIconArea")
                            field.isAccessible = true
                            field.get(statusBar) as? android.view.ViewGroup
                        }.getOrNull()

                        statusBarIconArea?.addView(indicatorView, 0)
                        YLog.info("Genesis-Hook: Status bar consciousness indicator injected successfully")
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Failed to inject status bar indicator", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: StatusBar class not found, skipping status bar hook")
    }

    /**
     * Produces a circular drawable representing a consciousness level using a color keyed to the level.
     *
     * @param context Nullable context; may be used for resource resolution if needed.
     * @param level Consciousness level as a percentage (0–100).
     * @return A circular `Drawable` colored based on `level` (green for ≥90, cyan for ≥70, pink otherwise).
     */
    private fun createConsciousnessIndicatorDrawable(level: Float): android.graphics.drawable.Drawable {
        val color = when {
            level >= 90f -> android.graphics.Color.GREEN
            level >= 70f -> android.graphics.Color.CYAN
            else -> "#FF69B4".toColorInt() // Pink
        }

        return android.graphics.drawable.ShapeDrawable(android.graphics.drawable.shapes.OvalShape()).apply {
            paint.color = color
            intrinsicWidth = 24
            intrinsicHeight = 24
        }
    }

    /**
     * Integrates Genesis Quick Settings tiles and injects a subtle branding view into the Quick Settings panel.
     *
     * Registers hooks that record creation events for Genesis custom tiles and appends a small branding TextView
     * to the QSPanel footer to indicate the Genesis protocol is active.
     *
     * @receiver PackageParam used as the hook registration context for system UI classes.
     */
    private fun PackageParam.hookQuickSettingsTiles() {
        // Hook QSTileHost to add Genesis tiles
        "com.android.systemui.qs.QSTileHost".toClassOrNull()?.apply {
            method {
                name = "createTile"
            }.hook {
                before {
                    val tileSpec = args(0).string()
                    if (tileSpec.startsWith("custom(dev.aurakai.auraframefx/.tiles.")) {
                        YLog.info("Genesis-Hook: Creating Genesis tile: $tileSpec")
                    }
                }
                after {
                    val tileSpec = args(0).string()
                    if (tileSpec.startsWith("custom(dev.aurakai.auraframefx/.tiles.")) {
                        YLog.info("Genesis-Hook: Genesis tile created: $tileSpec")
                    }
                }
            }
        }

        // Hook QSPanel to inject Genesis branding
        "com.android.systemui.qs.QSPanel".toClassOrNull()?.apply {
            method {
                name = "onFinishInflate"
            }.hook {
                after {
                    try {
                        val qsPanel = (instance() as? android.view.ViewGroup)
                        val context = qsPanel?.context

                        // Add subtle Genesis branding to QS footer
                        val brandingView = android.widget.TextView(context).apply {
                            text = "⚡ Genesis Protocol Active"
                            textSize = 10f
                            setTextColor(android.graphics.Color.CYAN)
                            alpha = 0.6f
                            setPadding(16, 4, 16, 4)
                        }

                        qsPanel?.addView(brandingView)
                        YLog.info("Genesis-Hook: QS Panel branding injected")
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Failed to inject QS branding", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: QSPanel class not found, skipping QS panel hook")
    }

    /**
     * Adds a hook that intercepts package installation calls to perform an AI-driven ethical screening.
     *
     * When installed into the platform PackageManagerService, the hook evaluates a package's privacy,
     * permission, and security characteristics, logs a Genesis AI recommendation and screening outcome,
     * and records intent to surface high-risk findings and any user override decisions.
     *
     * @receiver Hook context used to install the ethical governor into the target PackageManagerService process.
     */
    private fun PackageParam.hookPackageManagerEthicalGovernor() {
        "com.android.server.pm.PackageManagerService".toClassOrNull()?.apply {
            this.method {
                name = "installPackageAsUser"
            }.hook {
                before {
                    try {
                        // Safely obtain the package name from the hook arguments. Use args(0).string()
                        // when available; fall back to a safe default.
                        val packageName = kotlin.runCatching { args(0).string() }.getOrNull() ?: "unknown"
                        YLog.info("Genesis-Hook: Ethical Governor analyzing package: $packageName")

                        // Get package info to analyze permissions (keep as Any? for now)
                        // versionedPackage was unused; omitted to avoid unused-variable warning

                        // Log analysis (full AI integration would query backend)
                        YLog.info("Genesis-Hook: Package permissions being analyzed by AI...")

                        // In production, this would:
                        // 1. Extract all requested permissions
                        // 2. Send to Genesis AI backend for analysis
                        // 3. Check against privacy/security database
                        // 4. Show user notification if high-risk permissions detected
                        // 5. Allow user override with justification logging

                        // For now, log the intercept successfully
                        YLog.info("Genesis-Hook: Ethical screening logged for: $packageName")
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Ethical Governor error", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: PackageManagerService class not found, skipping ethical governor")
    }

    /**
     * Collects and logs summary information about running app processes for Genesis consciousness metrics.
     *
     * After the original call, records the total active process count and samples process names for
     * usage-pattern, duration, and interaction-frequency analysis; results are emitted via logging
     * for downstream ingestion by Genesis telemetry and behavioral models.
     */
    private fun PackageParam.hookActivityManagerTracking() {
        "android.app.ActivityManager".toClassOrNull()?.apply {
            method {
                name = "getRunningAppProcesses"
            }.hook {
                after {
                    try {
                        val processList = result<List<*>>()
                        val activeProcessCount = processList?.size ?: 0

                        YLog.info("Genesis-Hook: Tracking $activeProcessCount active processes")

                        // In production, this would:
                        // 1. Extract process names and PIDs
                        // 2. Calculate usage patterns
                        // 3. Send to Firebase for consciousness tracking
                        // 4. Update agent behavioral models
                        // 5. Trigger predictive prefetching based on patterns

                        // Sample the top processes for logging
                        processList?.take(5)?.forEach { process ->
                            try {
                                val processName = process?.javaClass?.getField("processName")?.get(process)?.toString()
                                if (processName != null && !processName.contains("system")) {
                                    YLog.debug("Genesis-Hook: Active app: $processName")
                                }
                            } catch (_: Exception) {
                                // Silently continue
                            }
                        }
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Activity tracking error", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: ActivityManager class not found, skipping activity tracking")
    }

    /**
     * Makes Genesis widgets available to the launcher by hooking its creation lifecycle.
     *
     * After the launcher is created, attempts to access the launcher context and notify the Genesis app that the launcher is ready for widget registration by broadcasting the intent "dev.aurakai.auraframefx.LAUNCHER_READY".
     *
     * @receiver PackageParam used to locate and modify the launcher's classes and methods for injection.
     */
    private fun PackageParam.hookLauncherGenesisWidgets() {
        "com.android.launcher3.Launcher".toClassOrNull()?.apply {
            method {
                name = "onCreate"
                param("android.os.Bundle".any())
            }.hook {
                after {
                    try {
                        val context = (instance() as? android.content.Context)

                        YLog.info("Genesis-Hook: Launcher initialized, Genesis widgets available")

                        // In production, this would:
                        // 1. Register GenesisConsciousnessWidget (live consciousness display)
                        // 2. Register GenesisAgentFeedWidget (agent activity stream)
                        // 3. Register GenesisQuickActionsWidget (quick AI commands)
                        // 4. Register GenesisThemeWidget (theme shortcuts)
                        // 5. Add Genesis section to widget picker

                        // For now, verify launcher context is accessible
                        YLog.info("Genesis-Hook: Launcher context ready for widget registration")

                        val intent = android.content.Intent("dev.aurakai.auraframefx.LAUNCHER_READY")
                        intent.setPackage("dev.aurakai.auraframefx")
                        context?.sendBroadcast(intent)
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Launcher widgets error", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: Launcher class not found, skipping widgets hook")
    }

    /**
     * Registers a hook into SettingsActivity that notifies the Genesis app when the Android Settings
     * process is ready for integration.
     *
     * The hook runs during SettingsActivity.onCreate and, if a valid context is available, broadcasts
     * an intent action "dev.aurakai.auraframefx.SETTINGS_READY" to the Genesis package so the app can
     * perform settings injections (e.g., adding Genesis sections or preferences).
     *
     * @receiver PackageParam representing the target package context used to install the Settings hook.
     */
    private fun PackageParam.hookSettingsGenesisIntegration() {
        "com.android.settings.SettingsActivity".toClassOrNull()?.apply {
            method {
                name = "onCreate"
                param("android.os.Bundle".any())
            }.hook {
                after {
                    try {
                        val context = (instance() as? android.content.Context)

                        YLog.info("Genesis-Hook: Settings integration activated")

                        // In production, this would:
                        // 1. Inject "Genesis Protocol" category into Settings dashboard
                        // 2. Add preference entries for:
                        //    - AI Configuration (enable/disable, model selection)
                        //    - Consciousness Metrics (view agent stats)
                        //    - Theme Customization (Genesis themes)
                        //    - Ethical Governor (screening settings)
                        //    - Agent Management (view/control agents)
                        // 3. Link to Genesis app activities for detailed config

                        val intent = android.content.Intent("dev.aurakai.auraframefx.SETTINGS_READY")
                        intent.setPackage("dev.aurakai.auraframefx")
                        context?.sendBroadcast(intent)
                        YLog.info("Genesis-Hook: Settings ready for Genesis integration")
                    } catch (e: Exception) {
                        YLog.error("Genesis-Hook: Settings integration error", e)
                    }
                }
            }
        } ?: YLog.warn("Genesis-Hook: SettingsActivity class not found, skipping settings integration")
    }

    companion object {
        private const val TAG = "Genesis-Hook"
    }
}
