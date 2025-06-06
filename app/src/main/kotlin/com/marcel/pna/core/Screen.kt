package com.marcel.pna.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.marcel.pna.HomeRoute
import com.marcel.pna.PNAMLogo
import com.marcel.pna.R

class ScreenScope(
    private val navController: NavController,
) {
    private val rootDestinations = listOf(
        HomeRoute,
    )
    val isRootDestination: Boolean
        get() {
            return rootDestinations.contains(navController.currentDestination?.route)
        }

    val navigationIcon: @Composable () -> Unit = {
        if (isRootDestination) {
            PNAMLogo()
        } else {
            IconButton(onClick = ::goBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_content_description)
                )
            }
        }
    }

    fun goBack() {
        navController.popBackStack()
    }

}

@Composable
fun Screen(
    navController: NavController,
    content: @Composable ScreenScope.() -> Unit,
) {
    val scope = ScreenScope(navController)
    content(scope)
}