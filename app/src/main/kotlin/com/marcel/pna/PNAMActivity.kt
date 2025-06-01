package com.marcel.pna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.marcel.pna.theme.PNAMTheme

class PNAMActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PNAMTheme {
                Text("this a test")
            }
        }
    }
}