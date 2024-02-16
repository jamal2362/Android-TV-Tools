package com.jamal2367.androidtvtools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.tananaev.adblib.AdbBase64
import com.tananaev.adblib.AdbConnection
import com.tananaev.adblib.AdbCrypto
import com.tananaev.adblib.AdbStream
import java.lang.ref.WeakReference
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.nio.ByteOrder

class MainActivity : Activity() {

    private var tvIP: TextView? = null
    private var connection: AdbConnection? = null
    private var stream: AdbStream? = null
    private var myAsyncTask: MyAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        if (!isUsbDebuggingEnabled()) {
            Toast.makeText(this, getString(R.string.enable_usb_debugging_first), Toast.LENGTH_LONG).show()
            openDeveloperSettings()
            finish()
            return
        }

        tvIP = findViewById(R.id.ip)
        tvIP?.text = getGateWayIp(this)

        findViewById<Button>(R.id.btnDeleteAppCaches).setOnClickListener {
            onKeyCE(10)
        }

        findViewById<Button>(R.id.btnSpeedProfile).setOnClickListener {
            onKeyCE(20)
        }

        findViewById<Button>(R.id.btnSpeedUpTransitions).setOnClickListener {
            onKeyCE(30)
        }

        findViewById<Button>(R.id.btnSuspendedCachedApplications).setOnClickListener {
            onKeyCE(40)
        }

        findViewById<Button>(R.id.btnSpeedManagementSystem).setOnClickListener {
            onKeyCE(50)
        }

        findViewById<Button>(R.id.btnDeviceInfo).setOnClickListener {
            onKeyCE(60)
        }
    }

    private fun onKeyCE(case: Int) {
        Toast.makeText(this, getString(R.string.allow_usb_debugging_dialog), Toast.LENGTH_SHORT).show()

        connection = null
        stream = null

        myAsyncTask?.cancel()
        myAsyncTask = MyAsyncTask(this)
        myAsyncTask?.execute(case)
    }

    fun adbCommander(ip: String?, case: Int) {
        val socket = Socket(ip, 5555)
        val generateAdbKeyPair = AdbCrypto.generateAdbKeyPair(AndroidBase64())

        try {
            if (stream == null || connection == null) {
                connection = AdbConnection.create(socket, generateAdbKeyPair)
                connection?.connect()
            }

            when (case) {
                10 -> {
                    Thread.sleep(500)
                    stream = connection?.open("shell:pm trim-caches 999999999999999999")
                    Log.d("MainActivity", "Case 10 executed")
                }
                20 -> {
                    Thread.sleep(500)
                    stream = connection?.open("shell:cmd package compile -m speed-profile -a")
                    Log.d("MainActivity", "Case 20 executed")
                }
                30 -> {
                    Thread.sleep(500)
                    stream = connection?.open("shell:settings put global window_animation_scale 0.5")
                    Log.d("MainActivity", "Case 30 executed A")
                    Thread.sleep(200)
                    stream = connection?.open("shell:settings put global transition_animation_scale 0.5")
                    Log.d("MainActivity", "Case 30 executed B")
                    Thread.sleep(200)
                    stream = connection?.open("shell:settings put global animator_duration_scale 0.5")
                    Log.d("MainActivity", "Case 30 executed C")
                }
                40 -> {
                    Thread.sleep(500)
                    stream = connection?.open("shell:settings put global cached_apps_freezer enabled")
                    Log.d("MainActivity", "Case 40 executed A")
                    Thread.sleep(250)
                    stream = connection?.open("shell:adb shell settings put secure tap_duration_threshold 0.0")
                    Log.d("MainActivity", "Case 40 executed B")
                    Thread.sleep(250)
                    stream = connection?.open("shell:settings put secure touch_blocking_period 0.0")
                    Log.d("MainActivity", "Case 40 executed C")
                }
                50 -> {
                    Thread.sleep(500)
                    stream = connection?.open("shell:settings put global sem_enhanced_cpu_responsiveness 1")
                    Log.d("MainActivity", "Case 50 executed")
                }
                60 -> {
                    Thread.sleep(500)
                    val manufacturerStream = connection?.open("shell:getprop ro.product.manufacturer")
                    val modelStream = connection?.open("shell:getprop ro.product.model")
                    val nameStream = connection?.open("shell:getprop ro.product.name")
                    val serialNoStream = connection?.open("shell:getprop ro.serialno")
                    val androidVersionStream = connection?.open("shell:getprop ro.build.version.release")
                    val sdkStream = connection?.open("shell:getprop ro.build.version.sdk")
                    val fingerprintStream = connection?.open("shell:getprop ro.bootimage.build.fingerprint")
                    val securityPatchStream = connection?.open("shell:getprop ro.build.version.security_patch")
                    val socStream = connection?.open("shell:getprop ro.product.device")
                    val archStream = connection?.open("shell:getprop ro.product.cpu.abi")
                    val bootloaderStream = connection?.open("shell:getprop ro.bootloader")
                    val lockedStream = connection?.open("shell:getprop ro.boot.vbmeta.device_state")
                    val resolutionStream = connection?.open("shell:wm size")
                    val dpiStream = connection?.open("shell:getprop ro.sf.lcd_density")
                    val incrementalStream = connection?.open("shell:getprop ro.build.version.incremental")
                    val memStream = connection?.open("shell:cat /proc/meminfo")

                    val manufacturerOutputBytes = manufacturerStream?.read()
                    val modelOutputBytes = modelStream?.read()
                    val nameOutputBytes = nameStream?.read()
                    val serialNoOutputBytes = serialNoStream?.read()
                    val androidVersionOutputBytes = androidVersionStream?.read()
                    val sdkOutputBytes = sdkStream?.read()
                    val fingerprintOutputBytes = fingerprintStream?.read()
                    val securityPatchOutputBytes = securityPatchStream?.read()
                    val socOutputBytes = socStream?.read()
                    val archOutputBytes = archStream?.read()
                    val bootloaderOutputBytes = bootloaderStream?.read()
                    val lockedOutputBytes = lockedStream?.read()
                    val resolutionOutputBytes = resolutionStream?.read()
                    val dpiOutputBytes = dpiStream?.read()
                    val incrementalOutputBytes = incrementalStream?.read()
                    val memOutputBytes = memStream?.read()

                    val manufacturerMessage: String = manufacturerOutputBytes?.decodeToString() ?: "No output available"
                    val modelMessage: String = modelOutputBytes?.decodeToString() ?: "No output available"
                    val nameMessage: String = nameOutputBytes?.decodeToString() ?: "No output available"
                    val serialNoMessage: String = serialNoOutputBytes?.decodeToString() ?: "No output available"
                    val androidVersionMessage: String = androidVersionOutputBytes?.decodeToString() ?: "No output available"
                    val sdkMessage: String = sdkOutputBytes?.decodeToString() ?: "No output available"
                    val fingerprintMessage: String = fingerprintOutputBytes?.decodeToString() ?: "No output available"
                    val securityPatchMessage: String = securityPatchOutputBytes?.decodeToString() ?: "No output available"
                    val socMessage: String = socOutputBytes?.decodeToString() ?: "No output available"
                    val archMessage: String = archOutputBytes?.decodeToString() ?: "No output available"
                    val bootloaderMessage: String = bootloaderOutputBytes?.decodeToString() ?: "No output available"
                    val lockedMessage: String = lockedOutputBytes?.decodeToString() ?: "No output available"
                    val resolutionMessage: String = resolutionOutputBytes?.decodeToString() ?: "No output available"
                    val dpiMessage: String = dpiOutputBytes?.decodeToString() ?: "No output available"
                    val incrementalMessage: String = incrementalOutputBytes?.decodeToString() ?: "No output available"
                    val memMessage: String = memOutputBytes?.decodeToString() ?: "No output available"

                    val combinedMessage = "Basic information:" +
                            "\nManufacturer: " + manufacturerMessage +
                            "Model: " + modelMessage +
                            "Device codename: " + nameMessage +
                            "Serial number: " + serialNoMessage +
                            "Android TV version: " + androidVersionMessage +
                            "Android SDK version: " + sdkMessage +
                            "Incremental version: " + incrementalMessage +
                            "Build fingerprint: " + fingerprintMessage +
                            "Security updates date: " + securityPatchMessage +
                            "\nOther information:" +
                            "\nSoC: " + socMessage +
                            "Architecture: " + archMessage +
                            "Bootloader version: " + bootloaderMessage +
                            "Bootloader state: " + lockedMessage +
                            resolutionMessage +
                            "Pixel density: " + dpiMessage +
                            "\nRAM information:\n"+
                            memMessage

                    Log.d("MainActivity", "Case 60 executed")

                    runOnUiThread {
                        val message: CharSequence = (combinedMessage)
                        AlertDialog.Builder(this)
                            .setMessage(message)
                            .show()
                    }
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Thread.currentThread().interrupt()
        }
    }

    private fun openDeveloperSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun isUsbDebuggingEnabled(): Boolean {
        return Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
    }

    private fun getWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private fun getGateWayIp(context: Context): String? {
        val int2String: String?
        val dhcpInfo = getDhcpInfo(context)!!
        int2String = if (dhcpInfo.ipAddress == 0) {
            localIp
        } else {
            int2String(dhcpInfo.ipAddress)
        }
        return int2String
    }

    private val localIp: String?
        get() {
            var str: String? = null
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val inetAddresses = networkInterfaces.nextElement().inetAddresses
                    while (inetAddresses.hasMoreElements()) {
                        val nextElement = inetAddresses.nextElement()
                        if (!nextElement.isLoopbackAddress && nextElement is Inet4Address) {
                            str = nextElement.getHostAddress()
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return str
        }


    private fun int2String(i: Int): String {
        return if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            (i and 255).toString() + "." + (i shr 8 and 255) + "." + (i shr 16 and 255) + "." + (i shr 24 and 255)
        } else (i shr 24 and 255).toString() + "." + (i shr 16 and 255) + "." + (i shr 8 and 255) + "." + (i and 255)
    }

    @Suppress("DEPRECATION")
    private fun getDhcpInfo(context: Context): DhcpInfo? {
        val wifiManager = getWifiManager(context)
        return wifiManager.dhcpInfo
    }

    class MyAsyncTask internal constructor(context: MainActivity) {
        private val activityReference: WeakReference<MainActivity> = WeakReference(context)
        private var thread: Thread? = null

        fun execute(case: Int) {
            thread = Thread {
                val activity = activityReference.get()
                activity?.adbCommander(activity.tvIP?.text.toString(), case)

                if (Thread.interrupted()) {
                    return@Thread
                }
            }
            thread?.start()
        }

        fun cancel() {
            thread?.interrupt()
        }
    }

    class AndroidBase64 : AdbBase64 {
        override fun encodeToString(bArr: ByteArray): String {
            return Base64.encodeToString(bArr, Base64.NO_WRAP)
        }
    }
}
