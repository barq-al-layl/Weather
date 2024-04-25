package com.ba.weather.ui

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ba.weather.R
import com.ba.weather.model.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    resources: Resources = resources(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(navController, snackbarHostState, resources, snackbarManager, coroutineScope) {
    AppState(navController, snackbarHostState, resources, snackbarManager, coroutineScope)
}

class AppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    resources: Resources,
    snackbarManager: SnackbarManager,
    coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.message.collectLatest {
                snackbarHostState.showSnackbar(message = resources.getString(it))
            }
        }
    }

    val bottomBarTabs = BottomBarItem.values()
    private val bottomBarRoutes = bottomBarTabs.map { it.route }

    val currentRoute: String?
        get() = navController.currentDestination?.route

    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState()
            .value?.destination?.route in bottomBarRoutes

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(bottomBarRoutes.first()) {
                    saveState = true
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

enum class BottomBarItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
) {
    Forecast(
        route = "home/forecast",
        icon = R.drawable.house,
        label = R.string.forecast,
    ),
    Search(
        route = "home/search",
        icon = R.drawable.search_location,
        label = R.string.search,
    ),
    Settings(
        route = "home/settings",
        icon = R.drawable.settings,
        label = R.string.settings,
    ),
}

object MainRoutes {
    const val Home = "home"
    const val Daily = "Daily"
}