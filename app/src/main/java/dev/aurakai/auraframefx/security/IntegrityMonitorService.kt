package dev.aurakai.auraframefx.security

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntegrityMonitorService : Service() {

    @Inject
    lateinit var securityContext: SecurityContext

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSignatureHash: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            while (true) {
                val integrity = securityContext.verifyApplicationIntegrity()
                if (lastSignatureHash != null && lastSignatureHash != integrity.signatureHash) {
                    securityContext.logSecurityEvent(
                        SecurityEvent(
                            type = SecurityEventType.INTEGRITY_CHECK,
                            details = "Application signature has changed!",
                            severity = EventSeverity.CRITICAL
                        )
                    )
                    val intent = Intent(ACTION_INTEGRITY_VIOLATION)
                    sendBroadcast(intent)
                }
                lastSignatureHash = integrity.signatureHash
                delay(MONITORING_INTERVAL_MS)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val MONITORING_INTERVAL_MS = 60000L // 1 minute
        const val ACTION_INTEGRITY_VIOLATION = "dev.aurakai.auraframefx.ACTION_INTEGRITY_VIOLATION"
    }
}
