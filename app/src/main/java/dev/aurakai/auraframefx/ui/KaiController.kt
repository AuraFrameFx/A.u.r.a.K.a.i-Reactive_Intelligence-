package dev.aurakai.auraframefx.ui

import timber.log.Timber

/**
 * Controller class for Kai UI elements and gesture interactions.
 *
 * Handles gesture-based interactions with the Kai assistant, including taps,
 * long presses, and swipe gestures for navigation and control.
 */
class KaiController {

    /**
     * Indicates if Kai features are currently active or visible.
     */
    var isActive: Boolean = false
        private set

    /**
     * Listener interface for Kai interaction events.
     * Implement this to receive callbacks for gesture-based Kai interactions.
     */
    interface KaiInteractionListener {
        fun onKaiTapped()
        fun onKaiLongPressed()
        fun onKaiSwipedLeft()
        fun onKaiSwipedRight()
    }

    private var interactionListener: KaiInteractionListener? = null

    init {
        Timber.d("KaiController initialized")
    }

    /**
     * Sets the listener for Kai interaction events.
     *
     * @param listener The listener to receive Kai interaction callbacks
     */
    fun setInteractionListener(listener: KaiInteractionListener) {
        interactionListener = listener
        Timber.d("KaiInteractionListener set")
    }

    /**
     * Handles a tap gesture on the Kai assistant.
     * Triggers quick activation or primary action.
     */
    fun onKaiTapped() {
        Timber.i("Kai tapped - activating quick action")
        interactionListener?.onKaiTapped()

        // Default behavior: toggle active state
        if (!isActive) {
            activate()
        }
    }

    /**
     * Handles a long press gesture on the Kai assistant.
     * Typically used for advanced options or contextual menus.
     */
    fun onKaiLongPressed() {
        Timber.i("Kai long pressed - showing advanced options")
        interactionListener?.onKaiLongPressed()

        // Default behavior: show Kai control panel or settings
    }

    /**
     * Handles a left swipe gesture on the Kai assistant.
     * Typically used for navigation or dismissal.
     */
    fun onKaiSwipedLeft() {
        Timber.i("Kai swiped left - navigating or dismissing")
        interactionListener?.onKaiSwipedLeft()

        // Default behavior: previous context or minimize
        if (isActive) {
            deactivate()
        }
    }

    /**
     * Handles a right swipe gesture on the Kai assistant.
     * Typically used for navigation or expansion.
     */
    fun onKaiSwipedRight() {
        Timber.i("Kai swiped right - navigating or expanding")
        interactionListener?.onKaiSwipedRight()

        // Default behavior: next context or maximize
        if (!isActive) {
            activate()
        }
    }

    /**
     * Retrieves the current Kai Notch Bar state.
     *
     * @return The active state of the Kai Notch Bar
     */
    fun getKaiNotchBar(): Boolean {
        return isActive
    }

    /**
     * Activates the Kai assistant and notifies listeners.
     */
    fun activate() {
        if (!isActive) {
            isActive = true
            Timber.i("Kai activated")
            // Trigger UI updates or animations
        }
    }

    /**
     * Deactivates the Kai assistant and notifies listeners.
     */
    fun deactivate() {
        if (isActive) {
            isActive = false
            Timber.i("Kai deactivated")
            // Trigger UI updates or animations
        }
    }

    /**
     * Cleans up resources used by the KaiController.
     * Removes listeners and resets state.
     */
    fun destroy() {
        Timber.d("KaiController destroyed")
        interactionListener = null
        isActive = false
    }
}
