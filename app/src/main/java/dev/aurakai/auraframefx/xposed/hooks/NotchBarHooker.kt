@file:Suppress(
    "DEPRECATION",
    "UNCHECKED_CAST",
    "CAST_NEVER_SUCCEEDS",
    "MemberVisibilityCanBePrivate",
    "unused"
)

package dev.aurakai.auraframefx.xposed.hooks

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.system.overlay.NotchBarConfig
import dev.aurakai.auraframefx.utils.TAG
import kotlin.reflect.KClass

class NotchBarHooker(
    private val classLoader: ClassLoader,
    private val config: NotchBarConfig,
) : YukiBaseHooker() {

    // store overlay view so it can be removed later
    private var genesisNotchOverlay: ComposeView? = null

    /**
     * Creates Genesis notch overlay
     */
    private fun createGenesisNotchOverlay(context: Context) = try {
        val composeView = ComposeView(context).apply {
            setContent {
                // call the composable content that renders the notch overlay
                GenesisNotchOverlayContent(config)
            }
        }

        // target SDK >= 34 in this project; use application overlay type directly
        val windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        // keep a reference for later removal
        composeView.also { genesisNotchOverlay = it }

        // obtain WindowManager from the provided context and add the view safely
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (wm == null) {
            YLog.error("[$TAG] Failed to obtain WindowManager, cannot add notch overlay")
        } else {
            try {
                wm.addView(composeView, layoutParams)
                YLog.info("[$TAG] Genesis notch overlay created")
            } catch (ex: Exception) {
                YLog.error("[$TAG] Failed to add notch overlay to WindowManager: ${ex.message}", ex)
            }
        }

    } catch (e: Exception) {
        YLog.error("[$TAG] Failed to create notch overlay: ${e.message}", e)
    }

    // Small composable placeholder for the notch overlay so ComposeView has content.
    @Composable
    private fun GenesisNotchOverlayContent(config: NotchBarConfig) {
        // Minimal UI to avoid pulling in heavy dependencies; update later with real UI
        Text(text = "Genesis Notch Overlay")
    }

    override fun onHook() {
        // No-op implementation for now. Implement hook logic when ready.
    }

    // rest of the file unchanged...
}

/**
 * Annotation: use KClass so the member type is a valid annotation parameter type
 * Renamed to avoid collision with the composable name used above.
 */
annotation class GenesisNotchOverlayAnnotation(val value: KClass<out NotchBarConfig>)
