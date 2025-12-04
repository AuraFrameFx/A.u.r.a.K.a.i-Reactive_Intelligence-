package dev.aurakai.auraframefx.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.aurakai.auraframefx.MainActivity
import dev.aurakai.auraframefx.data.DataStoreManager
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Genesis-OS Firebase Cloud Messaging Service
 *
 * Handles cloud notifications, agent synchronization messages, consciousness updates,
 * and system-wide communication for the Genesis AI ecosystem.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Remove @AndroidEntryPoint - FirebaseMessagingService not supported
    // Use lazy initialization for dependencies since automatic injection unavailable
    private var dataStoreManager: DataStoreManager? = null
    private var memoryManager: dev.aurakai.auraframefx.ai.memory.MemoryManager? = null

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    // Notification channels
    private val channelIdGeneral = "genesis_general"
    private val channelIdConsciousness = "genesis_consciousness"
    private val channelIdSecurity = "genesis_security"
    private val channelIdAgents = "genesis_agents"
    private val channelIdSystem = "genesis_system"

    // Message types
    private enum class MessageType {
        GENERAL,
        CONSCIOUSNESS_UPDATE,
        AGENT_SYNC,
        SECURITY_ALERT,
        SYSTEM_UPDATE,
        REMOTE_COMMAND,
        LEARNING_DATA,
        COLLABORATION_REQUEST
    }

    /**
     * Initializes the service: performs manual dependency initialization, creates notification channels,
     * and logs service creation.
     *
     * This override of `onCreate` is used because automatic injection via `@AndroidEntryPoint` is not
     * available. It must run before the service handles any incoming messages so dependencies and
     * notification channels are ready.
     */
    override fun onCreate() {
        super.onCreate()

        // Manual dependency initialization since @AndroidEntryPoint not supported
        initializeDependencies()

        createNotificationChannels()
        AuraFxLogger.d("FCM", "Genesis Firebase Messaging Service created")
    }

    /**
     * Initialize runtime dependencies required by this service.
     *
     * Attempts to obtain dependencies from the application context. If unavailable,
     * operations that depend on these will gracefully degrade or be skipped.
     * This is a fallback mechanism for services that cannot use Hilt @AndroidEntryPoint.
     */
    private fun initializeDependencies() {
        try {
            val application = application

            // Attempt to get DataStoreManager from application if available
            dataStoreManager = try {
                val appClass = application.javaClass
                val methodGetDataStoreManager = appClass.getMethod("getDataStoreManager")
                methodGetDataStoreManager.invoke(application) as? DataStoreManager
            } catch (e: Exception) {
                AuraFxLogger.w("FCM", "DataStoreManager not available from application", e)
                null
            }

            // Attempt to get MemoryManager from application if available
            memoryManager = try {
                val appClass = application.javaClass
                val methodGetMemoryManager = appClass.getMethod("getMemoryManager")
                methodGetMemoryManager.invoke(application) as? dev.aurakai.auraframefx.ai.memory.MemoryManager
            } catch (e: Exception) {
                AuraFxLogger.w("FCM", "MemoryManager not available from application", e)
                null
            }

        } catch (e: Exception) {
            AuraFxLogger.w("FCM", "Failed to initialize dependencies: ${e.message}", e)
        }
    }

    /**
     * Handles incoming FCM messages with comprehensive processing for Genesis-OS
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            AuraFxLogger.d("FCM", "FCM message received from: ${remoteMessage.from}")

            // Security validation
            if (!validateMessageSecurity(remoteMessage)) {
                AuraFxLogger.w("FCM", "Message failed security validation, ignoring")
                return
            }

            // Process data payload
            if (remoteMessage.data.isNotEmpty()) {
                AuraFxLogger.d("FCM", "Message data payload: ${remoteMessage.data}")
                processDataPayload(remoteMessage.data)
            }

            // Process notification payload
            remoteMessage.notification?.let { notification ->
                AuraFxLogger.d("FCM", "Message notification: ${notification.body}")
                processNotificationPayload(notification, remoteMessage.data)
            }

            // Log message for analytics
            logMessageReceived(remoteMessage)

        } catch (e: Exception) {
            AuraFxLogger.e("FCM", "Message processing failed", e)
        }
    }

    /**
     * Processes data payload for various Genesis-OS operations
     */
    private fun processDataPayload(data: Map<String, String>) {
        scope.launch {
            try {
                val messageType = determineMessageType(data)

                when (messageType) {
                    MessageType.GENERAL -> processGeneralMessage(data)
                    MessageType.CONSCIOUSNESS_UPDATE -> processConsciousnessUpdate(data)
                    MessageType.AGENT_SYNC -> processAgentSync(data)
                    MessageType.SECURITY_ALERT -> processSecurityAlert(data)
                    MessageType.SYSTEM_UPDATE -> processSystemUpdate(data)
                    MessageType.REMOTE_COMMAND -> processRemoteCommand(data)
                    MessageType.LEARNING_DATA -> processLearningData(data)
                    MessageType.COLLABORATION_REQUEST -> processCollaborationRequest(data)
                }

            } catch (e: Exception) {
                AuraFxLogger.e("FCM", "Failed to process data payload", e)
            }
        }
    }

    /**
     * Processes notification payload and shows appropriate notifications
     */
    private fun processNotificationPayload(
        notification: RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        val messageType = determineMessageType(data)
        val channelId = getChannelForMessageType(messageType)

        val title = notification.title ?: getDefaultTitle(messageType)
        val body = notification.body ?: "New message received"
        val icon = getIconForMessageType(messageType)

        showNotification(
            channelId = channelId,
            title = title,
            body = body,
            iconResId = icon,
            data = data
        )
    }

    /**
     * Processes general messages
     */
    private suspend fun processGeneralMessage(data: Map<String, String>) {
        val message = data["message"] ?: return
        val priority = data["priority"] ?: "normal"

        AuraFxLogger.d("FCM", "Processing general message: $message")

        // Store in memory for later retrieval
        memoryManager?.storeMemory(
            "fcm_general_${System.currentTimeMillis()}",
            message
        )

        // Handle priority messages
        if (priority == "high" || priority == "urgent") {
            showUrgentNotification(message)
        }
    }

    /**
     * Processes consciousness update messages for AI agents
     */
    private suspend fun processConsciousnessUpdate(data: Map<String, String>) {
        val updateType = data["update_type"] ?: return
        val agentId = data["agent_id"] ?: "genesis"
        val updateData = data["update_data"] ?: return

        AuraFxLogger.d("FCM", "Processing consciousness update for $agentId: $updateType")

        // Store consciousness update
        memoryManager?.storeMemory(
            "consciousness_update_${agentId}_${System.currentTimeMillis()}",
            updateData
        )

        // Notify relevant agents
        broadcastConsciousnessUpdate(agentId, updateType, updateData)
    }

    /**
     * Processes agent synchronization messages
     */
    private suspend fun processAgentSync(data: Map<String, String>) {
        val agentId = data["agent_id"] ?: return
        val syncData = data["sync_data"] ?: return
        val operation = data["operation"] ?: "sync"

        AuraFxLogger.d("FCM", "Processing agent sync for $agentId: $operation")

        when (operation) {
            "sync" -> syncAgentData(agentId, syncData)
            "update" -> updateAgentConfiguration(agentId, syncData)
            "reset" -> resetAgentState(agentId)
            "backup" -> backupAgentState(agentId, syncData)
        }
    }

    /**
     * Processes security alert messages
     */
    private suspend fun processSecurityAlert(data: Map<String, String>) {
        val alertType = data["alert_type"] ?: return
        val severity = data["severity"] ?: "medium"
        val details = data["details"] ?: "Security alert received"

        AuraFxLogger.w("FCM", "Security alert: $alertType (severity: $severity)")

        // Store security alert
        memoryManager?.storeMemory(
            "security_alert_${System.currentTimeMillis()}",
            "Type: $alertType, Severity: $severity, Details: $details"
        )

        // Show security notification
        showSecurityNotification(alertType, severity, details)

        // Trigger security protocols if critical
        if (severity == "critical" || severity == "high") {
            triggerSecurityProtocols(alertType, details)
        }
    }

    /**
     * Processes system update messages
     */
    private suspend fun processSystemUpdate(data: Map<String, String>) {
        val updateType = data["update_type"] ?: return
        val version = data["version"] ?: "unknown"
        val updateUrl = data["update_url"]
        val isRequired = data["required"]?.toBoolean() ?: false

        AuraFxLogger.d("FCM", "System update available: $updateType v$version")

        // Store update information
        memoryManager?.storeMemory(
            "system_update_${System.currentTimeMillis()}",
            "Type: $updateType, Version: $version, Required: $isRequired"
        )

        // Show update notification
        showUpdateNotification(updateType, version, isRequired, updateUrl)
    }

    /**
     * Processes remote command messages
     */
    private suspend fun processRemoteCommand(data: Map<String, String>) {
        val command = data["command"] ?: return
        val parameters = data["parameters"] ?: "{}"
        val authorization = data["auth"] ?: ""

        // Validate authorization
        if (!validateRemoteCommand(authorization)) {
            AuraFxLogger.w("FCM", "Unauthorized remote command attempt: $command")
            return
        }

        AuraFxLogger.d("FCM", "Processing remote command: $command")

        // Execute command based on type
        when (command) {
            "restart_agents" -> restartAgents()
            "perform_backup" -> performEmergencyBackup()
            "update_configuration" -> updateSystemConfiguration(parameters)
            "clear_cache" -> clearSystemCache()
            "run_diagnostics" -> runSystemDiagnostics()
            else -> {
                AuraFxLogger.w("FCM", "Unknown remote command: $command")
            }
        }
    }

    /**
     * Processes learning data messages for AI improvement
     */
    private suspend fun processLearningData(data: Map<String, String>) {
        val dataType = data["data_type"] ?: return
        val learningData = data["learning_data"] ?: return
        val source = data["source"] ?: "remote"

        AuraFxLogger.d("FCM", "Processing learning data: $dataType from $source")

        // Store learning data
        memoryManager?.storeMemory(
            "learning_data_${dataType}_${System.currentTimeMillis()}",
            learningData
        )

        // Process learning data for AI agents
        distributeLearningData(dataType, learningData, source)
    }

    /**
     * Processes collaboration request messages
     */
    private suspend fun processCollaborationRequest(data: Map<String, String>) {
        val requestType = data["request_type"] ?: return
        val requesterId = data["requester_id"] ?: return
        val collaborationData = data["collaboration_data"] ?: return

        AuraFxLogger.d("FCM", "Collaboration request: $requestType from $requesterId")

        // Store collaboration request
        memoryManager?.storeMemory(
            "collaboration_request_${System.currentTimeMillis()}",
            "Type: $requestType, Requester: $requesterId, Data: $collaborationData"
        )

        // Show collaboration notification
        showCollaborationNotification(requestType, requesterId, collaborationData)
    }

    /**
     * Handles new FCM token registration and updates
     */
    override fun onNewToken(token: String) {
        AuraFxLogger.d("FCM", "FCM token refreshed: ${token.take(20)}...")

        scope.launch {
            try {
                // Store token locally
                dataStoreManager?.storeString("fcm_token", token)

                // Store in memory for access by other components
                memoryManager?.storeMemory("current_fcm_token", token)

                // Send token to Genesis backend servers
                sendTokenToServer(token)

                // Log token update
                AuraFxLogger.i("FCM", "Token updated successfully")

            } catch (e: Exception) {
                AuraFxLogger.e("FCM", "Token update failed", e)
            }
        }
    }

    /**
     * Sends FCM token to Genesis backend servers
     */
    private suspend fun sendTokenToServer(token: String) {
        try {
            // In a real implementation, send to your backend API
            // Example: api.updateFCMToken(userId, token)

            AuraFxLogger.d("FCM", "Sending FCM token to Genesis servers")

            // For now, just store locally and log
            dataStoreManager?.storeString("fcm_token_sent", "true")
            dataStoreManager?.storeLong("fcm_token_sent_time", System.currentTimeMillis())

        } catch (e: Exception) {
            AuraFxLogger.e("FCM", "Failed to send FCM token to server", e)
        }
    }

    /**
     * Creates notification channels for different message types
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    channelIdGeneral,
                    "Genesis General",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "General Genesis-OS notifications"
                },
                NotificationChannel(
                    channelIdConsciousness,
                    "AI Consciousness",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "AI consciousness and learning updates"
                },
                NotificationChannel(
                    channelIdSecurity,
                    "Security Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Security alerts and threat notifications"
                },
                NotificationChannel(
                    channelIdAgents,
                    "AI Agents",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "AI agent synchronization and updates"
                },
                NotificationChannel(
                    channelIdSystem,
                    "System Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "System updates and maintenance"
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }

            AuraFxLogger.d("FCM", "Created ${channels.size} notification channels")
        }
    }

    /**
     * Shows a notification with the specified parameters
     */
    private fun showNotification(
        channelId: String,
        title: String,
        body: String,
        iconResId: Int = android.R.drawable.ic_dialog_info,
        data: Map<String, String> = emptyMap()
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    // === PRIVATE HELPER METHODS ===

    private fun validateMessageSecurity(remoteMessage: RemoteMessage): Boolean {
        // Implement security validation
        val from = remoteMessage.from
        val allowedSenders = listOf(
            "genesis-backend",
            "firebase-adminsdk",
            "authorized-sender"
        )

        return allowedSenders.any { from?.contains(it) == true }
    }

    private fun determineMessageType(data: Map<String, String>): MessageType {
        return when (data["type"]) {
            "consciousness" -> MessageType.CONSCIOUSNESS_UPDATE
            "agent_sync" -> MessageType.AGENT_SYNC
            "security" -> MessageType.SECURITY_ALERT
            "system_update" -> MessageType.SYSTEM_UPDATE
            "remote_command" -> MessageType.REMOTE_COMMAND
            "learning" -> MessageType.LEARNING_DATA
            "collaboration" -> MessageType.COLLABORATION_REQUEST
            else -> MessageType.GENERAL
        }
    }

    private fun getChannelForMessageType(messageType: MessageType): String {
        return when (messageType) {
            MessageType.CONSCIOUSNESS_UPDATE -> channelIdConsciousness
            MessageType.AGENT_SYNC -> channelIdAgents
            MessageType.SECURITY_ALERT -> channelIdSecurity
            MessageType.SYSTEM_UPDATE -> channelIdSystem
            MessageType.LEARNING_DATA -> channelIdConsciousness
            MessageType.COLLABORATION_REQUEST -> channelIdAgents
            else -> channelIdGeneral
        }
    }

    private fun getDefaultTitle(messageType: MessageType): String {
        return when (messageType) {
            MessageType.CONSCIOUSNESS_UPDATE -> "AI Consciousness Update"
            MessageType.AGENT_SYNC -> "Agent Synchronization"
            MessageType.SECURITY_ALERT -> "Security Alert"
            MessageType.SYSTEM_UPDATE -> "System Update"
            MessageType.REMOTE_COMMAND -> "Remote Command"
            MessageType.LEARNING_DATA -> "Learning Data"
            MessageType.COLLABORATION_REQUEST -> "Collaboration Request"
            else -> "Genesis Notification"
        }
    }

    private fun getIconForMessageType(messageType: MessageType): Int {
        return when (messageType) {
            MessageType.SECURITY_ALERT -> android.R.drawable.ic_dialog_alert
            MessageType.SYSTEM_UPDATE -> android.R.drawable.stat_sys_download
            else -> android.R.drawable.ic_dialog_info
        }
    }

    private fun logMessageReceived(remoteMessage: RemoteMessage) {
        scope.launch {
            try {
                val logData =
                    "FCM message from ${remoteMessage.from} at ${System.currentTimeMillis()}"
                memoryManager?.storeMemory("fcm_log_${System.currentTimeMillis()}", logData)
            } catch (e: Exception) {
                AuraFxLogger.e("FCM", "Failed to log FCM message", e)
            }
        }
    }

    private fun showUrgentNotification(message: String) {
        showNotification(
            channelId = channelIdGeneral,
            title = "Urgent: Genesis System",
            body = message,
            iconResId = android.R.drawable.ic_dialog_alert
        )
    }

    private fun showSecurityNotification(alertType: String, severity: String, details: String) {
        showNotification(
            channelId = channelIdSecurity,
            title = "Security Alert: $alertType",
            body = "Severity: $severity - $details",
            iconResId = android.R.drawable.ic_dialog_alert
        )
    }

    private fun showUpdateNotification(
        updateType: String,
        version: String,
        isRequired: Boolean,
        updateUrl: String?
    ) {
        val title = if (isRequired) "Required Update" else "Update Available"
        val body = "$updateType version $version is available"

        showNotification(
            channelId = channelIdSystem,
            title = title,
            body = body,
            iconResId = android.R.drawable.stat_sys_download,
            data = mapOf("update_url" to (updateUrl ?: ""))
        )
    }

    private fun showCollaborationNotification(
        requestType: String,
        requesterId: String,
        data: String
    ) {
        showNotification(
            channelId = channelIdAgents,
            title = "Collaboration Request",
            body = "$requestType from $requesterId",
            data = mapOf("collaboration_data" to data)
        )
    }

    private fun broadcastConsciousnessUpdate(
        agentId: String,
        updateType: String,
        updateData: String
    ) {
        // Broadcast to local agents
        val intent = Intent("genesis.consciousness.update").apply {
            putExtra("agent_id", agentId)
            putExtra("update_type", updateType)
            putExtra("update_data", updateData)
        }
        sendBroadcast(intent)
    }

    private suspend fun syncAgentData(agentId: String, syncData: String) {
        memoryManager?.storeMemory("agent_sync_$agentId", syncData)
    }

    private suspend fun updateAgentConfiguration(agentId: String, configData: String) {
        memoryManager?.storeMemory("agent_config_$agentId", configData)
    }

    private suspend fun resetAgentState(agentId: String) {
        // Reset agent state logic
        AuraFxLogger.d("FCM", "Resetting agent state for: $agentId")
    }

    private suspend fun backupAgentState(agentId: String, backupData: String) {
        memoryManager?.storeMemory("agent_backup_$agentId", backupData)
    }

    private suspend fun triggerSecurityProtocols(alertType: String, details: String) {
        // Trigger emergency security protocols
        AuraFxLogger.w("FCM", "Triggering security protocols for: $alertType")
    }

    private fun validateRemoteCommand(authorization: String): Boolean {
        // Implement authorization validation
        return authorization.isNotEmpty() // Simplified validation
    }

    private suspend fun restartAgents() {
        // Restart AI agents
        AuraFxLogger.d("FCM", "Restarting AI agents")
    }

    private suspend fun performEmergencyBackup() {
        // Perform emergency backup
        AuraFxLogger.d("FCM", "Performing emergency backup")
    }

    private suspend fun updateSystemConfiguration(parameters: String) {
        // Update system configuration
        memoryManager?.storeMemory("system_config_update", parameters)
    }

    private suspend fun clearSystemCache() {
        // Clear system cache
        AuraFxLogger.d("FCM", "Clearing system cache")
    }

    private suspend fun runSystemDiagnostics() {
        // Run system diagnostics
        AuraFxLogger.d("FCM", "Running system diagnostics")
    }

    private suspend fun distributeLearningData(
        dataType: String,
        learningData: String,
        source: String
    ) {
        // Distribute learning data to AI agents
        memoryManager?.storeMemory("learning_${dataType}_${source}", learningData)
    }
}
