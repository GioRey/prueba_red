package com.giorey.prueba_red

import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity: FlutterActivity(){
     private val CHANNEL = "foreground_service"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "startService" -> {
                        startForegroundService(Intent(this, MyService::class.java))
                        result.success("Service Started")
                    }
                    "stopService" -> {
                        stopService(Intent(this, MyService::class.java))
                        result.success("Service Stopped")
                    }
                    else -> result.notImplemented()
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission(this)

        // Obtiene la lista de redes WiFi configuradas
        val configuredNetworks = getConfiguredNetworks(this)

        if (configuredNetworks.isNotEmpty()) {
            // Conectarse a la primera red de la lista

            for (network in configuredNetworks) {
                println("SSID: ${network.BSSID} - ${network.SSID}")
            }

            val networkId = configuredNetworks[0].networkId
            val success = connectToConfiguredNetwork(this, networkId)

            if (success) {
                println("Conectado a la red: ${configuredNetworks[0].SSID}")
            } else {
                println("Error al conectarse a la red")
            }
        } else {
            println("No hay redes WiFi configuradas")
        }

    }

    private fun checkPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getConfiguredNetworks(context: Context): List<WifiConfiguration> {
        val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.configuredNetworks ?: emptyList()
    }

    private fun connectToConfiguredNetwork(context: Context, networkId: Int): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.enableNetwork(networkId, true) // true = desactiva otras redes
    }

}
