package com.marcel.pna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.marcel.pna.components.theme.PNAComponentsTheme

class ComponentsDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PNAComponentsTheme {
                Box(
                    Modifier.Companion.safeContentPadding()
                ) {
                    Box(
                        Modifier.Companion
                            .fillMaxSize()
                            .wrapContentSize()
                    ) {
                        Text("Hello PNA")
                    }

                }
            }
        }
    }
}