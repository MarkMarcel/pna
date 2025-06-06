package com.marcel.pna

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.marcel.pna.core.Screen
import com.marcel.pna.ui.home.HomeScreen
import com.marcel.pna.ui.usersettings.UserSettingsScreen

const val HomeRoute = "home"
const val UserSettingsRoute = "user_settings"

fun NavGraphBuilder.homeScreen(
    navController: NavController
) = composable(HomeRoute) {
    Screen(navController = navController) {
        HomeScreen(
            navigationIcon = navigationIcon,
            onGoToUserSettings = { navController.navigate(UserSettingsRoute) }
        )
    }
}

fun NavGraphBuilder.userSettingsScreen(
    navController: NavController
) = composable(UserSettingsRoute) {
    Screen(navController = navController) {
        UserSettingsScreen(
            navigationIcon = navigationIcon
        )
    }
}