package dev.aurakai.auraframefx.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.PriorityQueue
import java.util.concurrent.ConcurrentHashMap

/**
 * Data class representing a scheduled task.
 *
 * @param id Unique identifier for the task
 * @param name Human-readable task name
 * @param priority Task priority (higher values = higher priority)
 * @param scheduledTime Time in milliseconds when task should execute (System.currentTimeMillis())
 * @param action The task action to execute
 */
data class ScheduledTask(
    val id: String,
    val name: String,
    val priority: Int = 0,
    val scheduledTime: Long = System.currentTimeMillis(),
    val action: suspend () -> Unit
) : Comparable<ScheduledTask> {
    override fun compareTo(other: ScheduledTask): Int {
        // First compare by scheduled time, then by priority
        val timeComparison = scheduledTime.compareTo(other.scheduledTime)
        return if (timeComparison != 0) timeComparison else -priority.compareTo(other.priority)
    }
}

/**
 * Task scheduler with priority queue and time-based trigger support.
 *
 * Features:
 * - Priority-based task execution
 * - Time-based scheduling
 * - Concurrent task execution
 * - Task cancellation
 */
class TaskScheduler {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val taskQueue = PriorityQueue<ScheduledTask>()
    private val activeTasks = ConcurrentHashMap<String, Job>()
    private val queueLock = Any()

    init {
        Timber.d("TaskScheduler: Initialized")
        startScheduler()
    }

    /**
     * Schedules a task with a simple string identifier.
     * This is a convenience method for backward compatibility.
     *
     * @param task Task name/identifier
     */
    fun scheduleTask(task: String) {
        scheduleTask(
            ScheduledTask(
                id = "task_${System.currentTimeMillis()}",
                name = task,
                action = {
                    Timber.i("TaskScheduler: Executing task: $task")
                }
            )
        )
    }

    /**
     * Schedules a task for execution.
     *
     * @param task The scheduled task with priority and timing
     */
    fun scheduleTask(task: ScheduledTask) {
        synchronized(queueLock) {
            taskQueue.offer(task)
            Timber.d("TaskScheduler: Task scheduled - ${task.name} (priority: ${task.priority}, time: ${task.scheduledTime})")
        }
    }

    /**
     * Schedules a task to run after a delay.
     *
     * @param delayMs Delay in milliseconds
     * @param priority Task priority
     * @param name Task name
     * @param action Task action
     */
    fun scheduleDelayed(
        delayMs: Long,
        priority: Int = 0,
        name: String,
        action: suspend () -> Unit
    ) {
        val scheduledTime = System.currentTimeMillis() + delayMs
        scheduleTask(
            ScheduledTask(
                id = "delayed_${System.currentTimeMillis()}",
                name = name,
                priority = priority,
                scheduledTime = scheduledTime,
                action = action
            )
        )
    }

    /**
     * Cancels a running task by ID.
     *
     * @param taskId The task ID to cancel
     * @return true if task was cancelled, false if not found
     */
    fun cancelTask(taskId: String): Boolean {
        return activeTasks[taskId]?.let { job ->
            job.cancel()
            activeTasks.remove(taskId)
            Timber.d("TaskScheduler: Task cancelled - $taskId")
            true
        } ?: false
    }

    /**
     * Starts the scheduler loop that processes queued tasks.
     */
    private fun startScheduler() {
        scope.launch {
            Timber.i("TaskScheduler: Scheduler started")

            while (true) {
                delay(100) // Check queue every 100ms

                synchronized(queueLock) {
                    val currentTime = System.currentTimeMillis()

                    // Process all tasks that are ready
                    while (taskQueue.isNotEmpty() && taskQueue.peek()?.scheduledTime ?: Long.MAX_VALUE <= currentTime) {
                        val task = taskQueue.poll() ?: break

                        // Execute task in a separate coroutine
                        val job = scope.launch {
                            try {
                                Timber.i("TaskScheduler: Executing task - ${task.name}")
                                task.action()
                                Timber.d("TaskScheduler: Task completed - ${task.name}")
                            } catch (e: Exception) {
                                Timber.e(e, "TaskScheduler: Task failed - ${task.name}")
                            } finally {
                                activeTasks.remove(task.id)
                            }
                        }

                        activeTasks[task.id] = job
                    }
                }
            }
        }
    }

    /**
     * Returns the number of tasks currently in the queue.
     */
    fun getQueueSize(): Int = synchronized(queueLock) { taskQueue.size }

    /**
     * Returns the number of active (running) tasks.
     */
    fun getActiveTaskCount(): Int = activeTasks.size
}
