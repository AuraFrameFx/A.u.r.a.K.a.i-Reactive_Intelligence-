package dev.aurakai.auraframefx.system.monitor

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.RandomAccessFile
import javax.inject.Inject

@AndroidEntryPoint
class SystemMonitorService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    // System monitoring managers
    private lateinit var activityManager: ActivityManager
    private lateinit var connectivityManager: ConnectivityManager
    private var lastCpuInfo: CpuInfo? = null

    companion object {
        private const val TAG = "SystemMonitorService"
        private const val MONITORING_INTERVAL_MS = 60000L // 60 seconds
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("SystemMonitorService: Service created")

        // Initialize system monitoring resources
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        Timber.i("SystemMonitorService: Initialized system monitoring managers")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: SystemMonitorService started.")

        // Start monitoring tasks in a coroutine
        serviceScope.launch {
            monitorSystem()
        }

        // If the service is killed, restart it
        return START_STICKY
    }

    /**
     * Continuously monitors system metrics such as CPU, memory, battery, and network status while the service is active.
     *
     * Intended to run in a background coroutine, gathering and processing system information at regular intervals.
     */
    private suspend fun monitorSystem() {
        Timber.i("SystemMonitorService: Starting continuous system monitoring")

        while (serviceScope.isActive) {
            try {
                // Gather all system metrics
                val cpuUsage = getCpuUsage()
                val memoryUsage = getMemoryUsage()
                val batteryStatus = getBatteryStatus()
                val networkStatus = getNetworkStatus()

                // Report gathered metrics
                reportMetrics(cpuUsage, memoryUsage, batteryStatus, networkStatus)

            } catch (e: Exception) {
                Timber.e(e, "SystemMonitorService: Error during monitoring cycle")
            }

            delay(MONITORING_INTERVAL_MS)
        }
    }

    /**
     * Monitors CPU usage by reading /proc/stat.
     * Returns CPU usage percentage or -1 if unavailable.
     */
    private fun getCpuUsage(): Float {
        return try {
            val currentInfo = readCpuInfo()

            if (lastCpuInfo != null) {
                val totalDelta = currentInfo.total - lastCpuInfo!!.total
                val idleDelta = currentInfo.idle - lastCpuInfo!!.idle

                val usage = if (totalDelta > 0) {
                    ((totalDelta - idleDelta).toFloat() / totalDelta.toFloat()) * 100f
                } else {
                    0f
                }

                lastCpuInfo = currentInfo
                Timber.d("SystemMonitorService: CPU Usage: %.2f%%", usage)
                usage
            } else {
                lastCpuInfo = currentInfo
                0f
            }
        } catch (e: Exception) {
            Timber.e(e, "SystemMonitorService: Failed to read CPU usage")
            -1f
        }
    }

    /**
     * Reads CPU information from /proc/stat.
     */
    private fun readCpuInfo(): CpuInfo {
        RandomAccessFile("/proc/stat", "r").use { reader ->
            val line = reader.readLine()
            val tokens = line.split("\\s+".toRegex())

            val user = tokens[1].toLong()
            val nice = tokens[2].toLong()
            val system = tokens[3].toLong()
            val idle = tokens[4].toLong()
            val iowait = tokens[5].toLong()
            val irq = tokens[6].toLong()
            val softirq = tokens[7].toLong()

            val total = user + nice + system + idle + iowait + irq + softirq

            return CpuInfo(total, idle)
        }
    }

    /**
     * Monitors memory usage using ActivityManager.
     * Returns memory usage information.
     */
    private fun getMemoryUsage(): MemoryUsage {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMB = memoryInfo.totalMem / (1024 * 1024)
        val availableMB = memoryInfo.availMem / (1024 * 1024)
        val usedMB = totalMB - availableMB
        val usagePercent = ((usedMB.toFloat() / totalMB.toFloat()) * 100f)

        Timber.d("SystemMonitorService: Memory: ${usedMB}MB / ${totalMB}MB (%.2f%%)", usagePercent)

        return MemoryUsage(
            totalMB = totalMB,
            usedMB = usedMB,
            availableMB = availableMB,
            usagePercent = usagePercent,
            isLowMemory = memoryInfo.lowMemory
        )
    }

    /**
     * Monitors battery level and charging status.
     * Returns battery status information.
     */
    private fun getBatteryStatus(): BatteryStatus {
        val batteryIntent = applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val plugged = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1

        val batteryPercent = if (level >= 0 && scale > 0) {
            (level.toFloat() / scale.toFloat()) * 100f
        } else {
            -1f
        }

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargingSource = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Not Charging"
        }

        Timber.d("SystemMonitorService: Battery: %.1f%% - %s (%s)", batteryPercent, if (isCharging) "Charging" else "Discharging", chargingSource)

        return BatteryStatus(
            percent = batteryPercent,
            isCharging = isCharging,
            chargingSource = chargingSource
        )
    }

    /**
     * Monitors network connectivity status.
     * Returns network status information.
     */
    private fun getNetworkStatus(): NetworkStatus {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        val isConnected = capabilities != null
        val networkType = when {
            capabilities == null -> "None"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }

        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isMetered = !connectivityManager.isActiveNetworkMetered.not()

        Timber.d("SystemMonitorService: Network: $networkType - Connected: $isConnected, Internet: $hasInternet")

        return NetworkStatus(
            isConnected = isConnected,
            networkType = networkType,
            hasInternet = hasInternet,
            isMetered = isMetered
        )
    }

    /**
     * Reports gathered metrics by broadcasting to interested components.
     * In production, this would update a database, send to analytics, or notify UI.
     */
    private fun reportMetrics(
        cpuUsage: Float,
        memoryUsage: MemoryUsage,
        batteryStatus: BatteryStatus,
        networkStatus: NetworkStatus
    ) {
        Timber.i(
            "SystemMonitorService: METRICS REPORT\n" +
                    "  CPU: %.2f%%\n" +
                    "  Memory: ${memoryUsage.usedMB}MB / ${memoryUsage.totalMB}MB (%.2f%%) - Low: ${memoryUsage.isLowMemory}\n" +
                    "  Battery: %.1f%% - ${if (batteryStatus.isCharging) "Charging" else "Discharging"} (${batteryStatus.chargingSource})\n" +
                    "  Network: ${networkStatus.networkType} - Connected: ${networkStatus.isConnected}",
            cpuUsage,
            memoryUsage.usagePercent,
            batteryStatus.percent
        )

        // Broadcast metrics to interested components
        val metricsIntent = Intent("dev.aurakai.auraframefx.SYSTEM_METRICS").apply {
            putExtra("cpu_usage", cpuUsage)
            putExtra("memory_used_mb", memoryUsage.usedMB)
            putExtra("memory_total_mb", memoryUsage.totalMB)
            putExtra("memory_percent", memoryUsage.usagePercent)
            putExtra("battery_percent", batteryStatus.percent)
            putExtra("battery_charging", batteryStatus.isCharging)
            putExtra("network_type", networkStatus.networkType)
            putExtra("network_connected", networkStatus.isConnected)
            putExtra("timestamp", System.currentTimeMillis())
        }

        sendBroadcast(metricsIntent)
        Timber.d("SystemMonitorService: Metrics broadcast sent")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called, returning null as this is not a bound service.")
        // This service is not intended to be bound, so return null.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("SystemMonitorService: Service destroyed, releasing resources")

        // Cancel all coroutines
        serviceJob.cancel()

        // Clear CPU info cache
        lastCpuInfo = null

        Timber.d("SystemMonitorService: All resources released")
    }
}

/**
 * Data class for CPU information from /proc/stat.
 */
private data class CpuInfo(
    val total: Long,
    val idle: Long
)

/**
 * Data class for memory usage information.
 */
private data class MemoryUsage(
    val totalMB: Long,
    val usedMB: Long,
    val availableMB: Long,
    val usagePercent: Float,
    val isLowMemory: Boolean
)

/**
 * Data class for battery status information.
 */
private data class BatteryStatus(
    val percent: Float,
    val isCharging: Boolean,
    val chargingSource: String
)

/**
 * Data class for network status information.
 */
private data class NetworkStatus(
    val isConnected: Boolean,
    val networkType: String,
    val hasInternet: Boolean,
    val isMetered: Boolean
)
