package com.giorey.prueba_red

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent

import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat



class MyService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val interval = 5000L

    private val runnable = object : Runnable {
        override fun run() {
            Log.d("MyForegroundService", "Ejecutando tarea en segundo plano...")

            handler.postDelayed(this, interval)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio en Segundo Plano")
            .setContentText("Mi aplicación sigue ejecutándose.")
            .setSmallIcon(R.mipmap.ic_launcher)  // Reemplaza con tu icono
            .build()
        handler.post(runnable)
        startForeground(1, notification)


        connectToWifi(this, "Megacable_2.4G_8540", "HXbapQRb")

        return START_STICKY  // Hace que el servicio se reinicie si Android lo detiene.
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel() {
  //      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
//        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        connectToWifi(this, "Megacable_5G_8540", "HXbapQRb")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    fun connectToWifi(context: Context, ssid: String, password: String) {

    }





}
