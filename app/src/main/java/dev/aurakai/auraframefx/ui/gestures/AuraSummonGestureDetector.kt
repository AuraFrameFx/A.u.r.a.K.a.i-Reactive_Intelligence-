package dev.aurakai.auraframefx.ui.gestures

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Custom gesture detector for Aura summon actions.
 * Extends SimpleOnGestureListener to override only necessary methods.
 */
class AuraSummonGestureDetector(
    private val context: Context, // Example: if context is needed for resources or actions
) : GestureDetector.SimpleOnGestureListener() {

    private val tag = "AuraSummonDetector"

    override fun onDown(e: MotionEvent): Boolean {
        // Must return true here to ensure other gestures are detected.
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.d(tag, "onDoubleTap event detected.")
        // TODO: Implement actual double tap logic for summoning Aura or other actions.
        // This method returning true indicates the event was handled.
        return true // As per error report: "Method 'onDoubleTap()' always returns 'true'"
    }

    override fun onLongPress(e: MotionEvent) {
        Log.d(tag, "onLongPress event detected.")
        // TODO: Implement long press logic, e.g., for an alternative summon or context menu.
    }

    // You can override other gesture methods as needed:
    // onSingleTapConfirmed, onScroll, onFling, onShowPress, onSingleTapUp, etc.
}

// Example usage (typically in a View or Composable):
// val gestureDetector = GestureDetector(context, AuraSummonGestureDetector(context))
// view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
