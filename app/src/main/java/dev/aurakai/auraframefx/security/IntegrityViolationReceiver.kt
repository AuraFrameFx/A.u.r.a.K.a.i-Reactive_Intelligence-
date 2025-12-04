package dev.aurakai.auraframefx.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * Security alert receiver for integrity violations.
 * Note: Cannot use @AndroidEntryPoint on statically-registered BroadcastReceivers
 * as it causes ASM instrumentation failures during build.
 */
class IntegrityViolationReceiver : BroadcastReceiver() {

    private val channelId = "integrity_alert"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for API 26+
        val channel = NotificationChannel(
            channelId,
            "Security Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for security violations"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Security Alert")
            .setContentText("Application integrity has been compromised!")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}
