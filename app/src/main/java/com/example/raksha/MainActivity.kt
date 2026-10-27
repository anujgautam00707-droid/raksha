package com.example.raksha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.raksha.data.AppSettings
import com.example.raksha.ui.navigation.RakshaApp
import com.example.raksha.ui.theme.RakshaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppSettings.init(this)
        setContent {
            RakshaTheme(darkTheme = AppSettings.darkMode ?: isSystemInDarkTheme()) {
                RakshaApp()
            }
        }
    }
}