package com.example.raksha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.raksha.ui.navigation.RakshaApp
import com.example.raksha.ui.theme.RakshaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RakshaTheme {
                RakshaApp()
            }
        }
    }
}