package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.backup.JsonHelper
import com.example.data.model.SantriEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mutqin Ulul Albab", appName)
    }

    @Test
    fun `test Santri Json Serialization with Android JSONObject`() {
        val santri = SantriEntity(
            id = 10,
            nis = "UA2025099",
            namaLengkap = "Ahmad Dani",
            program = "Tahfidz Khusus",
            status = "Aktif"
        )
        val json = JsonHelper.santriToJson(santri)
        assertEquals("UA2025099", json.getString("nis"))
        assertEquals("Ahmad Dani", json.getString("namaLengkap"))

        val restored = JsonHelper.jsonToSantri(json)
        assertEquals(santri.nis, restored.nis)
        assertEquals(santri.namaLengkap, restored.namaLengkap)
        assertEquals(santri.status, restored.status)
    }
}
