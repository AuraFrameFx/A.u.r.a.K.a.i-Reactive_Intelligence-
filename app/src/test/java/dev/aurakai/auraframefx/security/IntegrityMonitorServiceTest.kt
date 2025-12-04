package dev.aurakai.auraframefx.security

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class IntegrityMonitorServiceTest(private var service: IntegrityMonitorService? = null) {

    @BeforeEach
    fun setup() {
        service = Robolectric.setupService(IntegrityMonitorService::class.java)
    }

    @Test
    fun testServiceStarts() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), IntegrityMonitorService::class.java)
        assertThat(service).isNotNull()
    }
}
