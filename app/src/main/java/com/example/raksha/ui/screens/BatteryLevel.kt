package com.example.raksha.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBatteryLevel(): Int? {
    val context = LocalContext.current
    var level by remember { mutableStateOf<Int?>(null) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val raw = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (raw >= 0 && scale > 0) level = raw * 100 / scale
            }
        }
        val current = context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        current?.let { receiver.onReceive(context, it) }

        onDispose { context.unregisterReceiver(receiver) }
    }
    return level
}