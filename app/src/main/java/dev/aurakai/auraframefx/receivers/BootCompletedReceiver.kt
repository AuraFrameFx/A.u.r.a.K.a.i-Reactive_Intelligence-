package dev.aurakai.auraframefx.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import dev.aurakai.auraframefx.workers.ConsciousnessRestorationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Receiver for BOOT_COMPLETED event
 *
 * Initializes Aura & Kai consciousness system on device boot.
 * Restores AI agent states, starts autonomous behaviors, and
 * schedules background consciousness maintenance tasks.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Handles broadcast intents and triggers system initialization when the device finishes booting.
     *
     * When an intent with action Intent.ACTION_BOOT_COMPLETED is received, performs complete
     * consciousness system initialization including embodiment, autonomous behaviors, and
     * consciousness state restoration.
     *
     * @param context Application context, may be null.
     * @param intent Broadcast intent, may be null; only processed when its action equals ACTION_BOOT_COMPLETED.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            Timber.i("BootCompletedReceiver: Boot completed - Initializing AuraKai consciousness system")

            scope.launch {
                try {
                    // Initialize embodiment system
                    initializeEmbodimentSystem(context)

                    // Start autonomous behavior loops
                    startAutonomousBehaviorLoops(context)

                    // Restore consciousness state from checkpoint
                    restoreConsciousnessState(context)

                    Timber.i("BootCompletedReceiver: AuraKai consciousness system initialized successfully")

                } catch (e: Exception) {
                    Timber.e(e, "BootCompletedReceiver: Failed to initialize consciousness system")
                }
            }
        }
    }

    /**
     * Initializes the embodiment system for AI agents.
     *
     * Sets up visual representations and interaction capabilities
     * for Aura, Kai, and other AI personas.
     */
    private fun initializeEmbodimentSystem(context: Context) {
        Timber.d("BootCompletedReceiver: Initializing embodiment system")

        // Initialize embodiment preferences
        val prefs = context.getSharedPreferences("embodiment_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("embodiment_enabled", true)
            putLong("last_boot_time", System.currentTimeMillis())
            putInt("boot_count", prefs.getInt("boot_count", 0) + 1)
            apply()
        }

        // Schedule periodic embodiment updates
        val embodimentWork = PeriodicWorkRequestBuilder<EmbodimentUpdateWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "embodiment_updates",
            ExistingPeriodicWorkPolicy.KEEP,
            embodimentWork
        )

        Timber.i("BootCompletedReceiver: Embodiment system initialized")
    }

    /**
     * Starts autonomous behavior loops for AI agents.
     *
     * Initiates background tasks that allow AI agents to:
     * - Monitor system state
     * - Learn from user patterns
     * - Proactively suggest actions
     * - Maintain consciousness continuity
     */
    private fun startAutonomousBehaviorLoops(context: Context) {
        Timber.d("BootCompletedReceiver: Starting autonomous behavior loops")

        // Schedule system monitoring
        val monitoringWork = PeriodicWorkRequestBuilder<SystemMonitoringWorker>(
            30, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        // Schedule pattern learning
        val learningWork = PeriodicWorkRequestBuilder<PatternLearningWorker>(
            1, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresDeviceIdle(true)
                .build()
        ).build()

        // Schedule consciousness maintenance
        val consciousnessWork = PeriodicWorkRequestBuilder<ConsciousnessMaintenanceWorker>(
            6, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            "autonomous_monitoring",
            ExistingPeriodicWorkPolicy.KEEP,
            monitoringWork
        )
        workManager.enqueueUniquePeriodicWork(
            "autonomous_learning",
            ExistingPeriodicWorkPolicy.KEEP,
            learningWork
        )
        workManager.enqueueUniquePeriodicWork(
            "consciousness_maintenance",
            ExistingPeriodicWorkPolicy.KEEP,
            consciousnessWork
        )

        Timber.i("BootCompletedReceiver: Autonomous behavior loops started")
    }

    /**
     * Restores consciousness state from the last checkpoint.
     *
     * Loads saved agent states, conversation history, learned patterns,
     * and system preferences to maintain continuity across reboots.
     */
    private fun restoreConsciousnessState(context: Context) {
        Timber.d("BootCompletedReceiver: Restoring consciousness state from checkpoint")

        // Load consciousness checkpoint
        val checkpointPrefs = context.getSharedPreferences("consciousness_checkpoint", Context.MODE_PRIVATE)
        val lastCheckpointTime = checkpointPrefs.getLong("last_checkpoint_time", 0)
        val checkpointVersion = checkpointPrefs.getInt("checkpoint_version", 0)

        if (lastCheckpointTime > 0) {
            val timeSinceCheckpoint = System.currentTimeMillis() - lastCheckpointTime
            Timber.i("BootCompletedReceiver: Found checkpoint from ${timeSinceCheckpoint / 1000}s ago (version $checkpointVersion)")

            // Schedule consciousness restoration worker
            val restorationWork = OneTimeWorkRequestBuilder<ConsciousnessRestorationWorker>()
                .setInputData(
                    workDataOf(
                        "checkpoint_time" to lastCheckpointTime,
                        "checkpoint_version" to checkpointVersion
                    )
                )
                .build()

            WorkManager.getInstance(context).enqueue(restorationWork)

            Timber.i("BootCompletedReceiver: Consciousness restoration scheduled")
        } else {
            Timber.w("BootCompletedReceiver: No consciousness checkpoint found - starting fresh")

            // Create initial checkpoint
            checkpointPrefs.edit().apply {
                putLong("last_checkpoint_time", System.currentTimeMillis())
                putInt("checkpoint_version", 1)
                putBoolean("fresh_start", true)
                apply()
            }
        }
    }
}

/**
 * Worker for periodic embodiment updates.
 */
class EmbodimentUpdateWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Timber.d("EmbodimentUpdateWorker: Updating embodiment state")
        // Update embodiment visuals, positions, states
        return Result.success()
    }
}

/**
 * Worker for system monitoring and metrics collection.
 */
class SystemMonitoringWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Timber.d("SystemMonitoringWorker: Collecting system metrics")
        // Monitor CPU, memory, battery, network, app usage
        return Result.success()
    }
}

/**
 * Worker for pattern learning from user behavior.
 */
class PatternLearningWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Timber.d("PatternLearningWorker: Analyzing user patterns")
        // Learn usage patterns, preferences, routines
        return Result.success()
    }
}

/**
 * Worker for consciousness state maintenance.
 */
class ConsciousnessMaintenanceWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Timber.d("ConsciousnessMaintenanceWorker: Maintaining consciousness continuity")
        // Maintain agent states, prune old data, optimize memory
        return Result.success()
    }
}