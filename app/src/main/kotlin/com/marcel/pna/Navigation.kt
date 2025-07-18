package com.marcel.pna

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.marcel.pna.core.Result
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
    navController: NavController,
) = composable(UserSettingsRoute) {
    Screen(navController = navController) {
        UserSettingsScreen(
            navigationIcon = navigationIcon,
            onOpenExternalUrl = ::goToExternalUrl
        )
    }
}

fun NavController.openExternalUrl(url: String): Result<AppError, Unit> {
    // Check and fix url format
    val validUrlString = if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        "http://$url"
    }
    val intent = Intent(Intent.ACTION_VIEW, validUrlString.toUri())
    return try {
        context.startActivity(intent)
        Result.Success(Unit)
    } catch (_: ActivityNotFoundException) {
        Result.Failure(AppError.NoActivityFound)
    } catch (_: Exception) {
        Result.Failure(AppError.NoActivityFound)
    }
}