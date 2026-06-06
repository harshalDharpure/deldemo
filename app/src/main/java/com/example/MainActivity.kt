package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.AndawalaAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AndawalaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<AndawalaViewModel>()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndawalaAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}
