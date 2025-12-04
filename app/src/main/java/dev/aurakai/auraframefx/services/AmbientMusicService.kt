package dev.aurakai.auraframefx.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.data.DataStoreManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Genesis-OS Ambient Music Service
 * Provides background ambient music and soundscape management for the AI consciousness experience
 *
 * Features:
 * - Foreground service for background audio playback (Android 8.0+ compliance)
 * - Audio focus handling for proper interaction with other audio apps
 * - Notification controls for playback management
 */
@AndroidEntryPoint
open class AmbientMusicService : Service() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume = 0.5f
    private var isShuffling = false
    private val trackHistory = mutableListOf<String>()
    private var currentTrack: String? = null

    // Audio focus management
    private lateinit var audioManager: AudioManager
    private var hasAudioFocus = false

    // Ambient tracks for Genesis-OS experience
    private val ambientTracks = listOf(
        "genesis_consciousness_ambient",
        "digital_meditation",
        "cyber_rain",
        "neural_waves",
        "quantum_stillness"
    )

    private val binder = AmbientMusicBinder()

    inner class AmbientMusicBinder : Binder() {
        fun getService(): AmbientMusicService = this@AmbientMusicService
    }

    // Audio focus change listener
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Timber.d("Audio focus gained")
                if (!isPlaying() && hasAudioFocus) {
                    resume()
                    setVolume(currentVolume) // Restore volume
                }
                hasAudioFocus = true
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Timber.d("Audio focus lost permanently")
                pause()
                hasAudioFocus = false
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.d("Audio focus lost temporarily")
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.d("Audio focus lost temporarily (can duck)")
                // Lower the volume instead of pausing
                mediaPlayer?.setVolume(0.1f, 0.1f)
            }
        }
    }

    /**
     * Called when a client attempts to bind to the service.
     * Returns binder for service communication.
     */
    override fun onBind(intent: Intent?): IBinder {
        Timber.d("AmbientMusicService bound")
        return binder
    }

    /**
     * Handles service start command and initializes ambient music system.
     * Starts service in foreground for background audio playback.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Starting AmbientMusicService")

        // Start foreground service with notification (required for background playback)
        startForeground(NOTIFICATION_ID, createNotification(currentTrack ?: "Starting..."))

        try {
            initializeAmbientMusic()

            // Auto-start ambient music if enabled in preferences
            intent?.let {
                val autoStart = it.getBooleanExtra("auto_start", false)
                if (autoStart && !isPlaying()) {
                    playRandomTrack()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to start ambient music service")
        }

        return START_STICKY // Keep service running for continuous ambient experience
    }

    /**
     * Pauses ambient music playback and releases audio focus.
     */
    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    Timber.d("Ambient music paused")
                    updateNotification(currentTrack ?: "Paused")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to pause ambient music")
        }
    }

    /**
     * Resumes ambient music playback if audio focus is available.
     */
    fun resume() {
        try {
            if (!requestAudioFocus()) {
                Timber.w("Could not get audio focus. Playback not resumed.")
                return
            }

            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    Timber.d("Ambient music resumed")
                    updateNotification(currentTrack ?: "Playing...")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to resume ambient music")
        }
    }

    /**
     * Sets the volume for ambient music.
     * @param volume Volume level between 0.0 and 1.0
     */
    fun setVolume(volume: Float) {
        try {
            val clampedVolume = volume.coerceIn(0.0f, 1.0f)
            currentVolume = clampedVolume
            mediaPlayer?.setVolume(clampedVolume, clampedVolume)
            Timber.d("Ambient music volume set to $clampedVolume")
        } catch (e: Exception) {
            Timber.e(e, "Failed to set ambient music volume")
        }
    }

    /**
     * Enables or disables shuffle mode for ambient tracks.
     */
    fun setShuffling(shuffling: Boolean) {
        isShuffling = shuffling
        Timber.d("Ambient music shuffle: $shuffling")
    }

    /**
     * Gets the currently playing ambient track.
     */
    fun getCurrentTrack(): String? {
        return currentTrack
    }

    /**
     * Gets the history of played ambient tracks.
     */
    fun getTrackHistory(): List<String> {
        return trackHistory.toList()
    }

    /**
     * Checks if music is currently playing.
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    /**
     * Skips to the next ambient track.
     */
    fun skipToNextTrack() {
        try {
            if (isShuffling) {
                playRandomTrack()
            } else {
                playNextTrack()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to skip to next track")
        }
    }

    /**
     * Skips to the previous ambient track.
     */
    fun skipToPreviousTrack() {
        try {
            if (trackHistory.size > 1) {
                // Remove current track and get previous
                trackHistory.removeLastOrNull()
                val previousTrack = trackHistory.lastOrNull()
                previousTrack?.let { playTrack(it) }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to skip to previous track")
        }
    }

    // === PRIVATE HELPER METHODS ===

    /**
     * Initializes the ambient music system and audio manager.
     */
    private fun initializeAmbientMusic() {
        try {
            Timber.d("Initializing ambient music system")
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Initialize MediaPlayer for ambient tracks
            // In a real implementation, you would load actual audio files
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize ambient music")
        }
    }

    /**
     * Requests audio focus for music playback.
     * @return true if audio focus was granted, false otherwise
     */
    private var audioFocusRequest: AudioFocusRequest? = null

    private fun requestAudioFocus(): Boolean {
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()

        val result = audioManager.requestAudioFocus(audioFocusRequest!!)
        hasAudioFocus = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        return hasAudioFocus
    }

    /**
     * Creates a notification for the foreground service.
     * @param trackName The name of the currently playing track
     */
    private fun createNotification(trackName: String): Notification {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Ambient Music",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Genesis-OS Ambient Music Playback"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Create intent to open the app when notification is tapped
        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Genesis-OS Ambient Music")
            .setContentText(trackName)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Add proper music icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    /**
     * Updates the notification with new track information.
     * @param trackName The name of the track to display
     */
    private fun updateNotification(trackName: String) {
        val notification = createNotification(trackName)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun playRandomTrack() {
        if (ambientTracks.isNotEmpty()) {
            val randomTrack = ambientTracks.random()
            playTrack(randomTrack)
        }
    }

    private fun playNextTrack() {
        currentTrack?.let { current ->
            val currentIndex = ambientTracks.indexOf(current)
            val nextIndex = (currentIndex + 1) % ambientTracks.size
            playTrack(ambientTracks[nextIndex])
        } ?: playRandomTrack()
    }

    private fun playTrack(trackName: String) {
        if (!requestAudioFocus()) {
            Timber.w("Could not get audio focus. Playback aborted.")
            return
        }

        try {
            Timber.d("Playing ambient track: $trackName")

            currentTrack = trackName
            trackHistory.add(trackName)

            // Keep history manageable
            if (trackHistory.size > MAX_TRACK_HISTORY_SIZE) {
                trackHistory.removeAt(0)
            }

            // Update notification with new track
            updateNotification(trackName)

            // In a real implementation, load and play the actual audio file
            // mediaPlayer = MediaPlayer.create(this, resourceId)
            // mediaPlayer?.start()

        } catch (e: Exception) {
            Timber.e(e, "Failed to play track: $trackName")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer?.release()
            mediaPlayer = null

            // Release audio focus
            if (::audioManager.isInitialized) {
                audioManager.abandonAudioFocus(audioFocusChangeListener)
            }

            Timber.d("AmbientMusicService destroyed")
        } catch (e: Exception) {
            Timber.e(e, "Error destroying ambient music service")
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "AmbientMusicChannel"
        private const val MAX_TRACK_HISTORY_SIZE = 20
    }
}
