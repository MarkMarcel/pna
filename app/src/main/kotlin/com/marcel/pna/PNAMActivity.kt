package com.marcel.pna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.marcel.pna.theme.PNAMTheme

class PNAMActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PNAMTheme {
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute
                ) {
                    homeScreen(navController = navController)
                    userSettingsScreen(navController = navController)
                }
            }
        }
    }
}