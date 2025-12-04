package com.zybooks.ai4sarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zybooks.ai4sarapp.ui.SarApp
import com.zybooks.ai4sarapp.ui.theme.AI4SARAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AI4SARAppTheme {
                SarApp()
            }
        }
    }
}
