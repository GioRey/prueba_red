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
        if (!Settings.System.canWrite(this)) {
            requestWriteSettingsPermission(this)
        }
    }

    private fun requestWriteSettingsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

}
