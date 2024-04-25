package com.ba.weather.ui

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ba.weather.R
import com.ba.weather.model.AlertDialogContent
import com.ba.weather.model.AlertDialogManager
import com.ba.weather.ui.forecast.ForecastScreen
import com.ba.weather.ui.search.SearchScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherApp() {
    val appState = rememberAppState()
    val systemUiController = rememberSystemUiController()
    var dialogContent by remember { mutableStateOf<AlertDialogContent?>(null) }
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
    )
    SideEffect {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(Unit) {
        AlertDialogManager.content.collect { content ->
            dialogContent = content
        }
    }

    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                systemUiController.setNavigationBarColor(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                )
                CustomBottomBar(
                    items = appState.bottomBarTabs,
                    currentRoute = appState.currentRoute!!,
                    onItemClick = appState::navigateToBottomBarRoute,
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = MainRoutes.Home,
            modifier = Modifier.padding(innerPadding),
        ) {
            navigation(
                startDestination = BottomBarItem.Forecast.route,
                route = MainRoutes.Home,
            ) {
                composable(route = BottomBarItem.Forecast.route) {
                    ForecastScreen(
                        navigateToSearch = {
                            appState.navigateToBottomBarRoute(BottomBarItem.Search.route)
                        }
                    )
                }
                composable(route = BottomBarItem.Search.route) {
                    SearchScreen(
                        navigateToForecast = {
                            appState.navigateToBottomBarRoute(BottomBarItem.Forecast.route)
                        },
                    )
                }
                composable(route = BottomBarItem.Settings.route) {

                }
            }
            composable(route = MainRoutes.Daily) {

            }
        }
    }
    dialogContent?.let { content ->
        AlertDialog(
            onDismissRequest = { dialogContent = null },
            confirmButton = {
                TextButton(
                    onClick = { dialogContent = null },
                ) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            title = {
                Text(
                    text = stringResource(id = content.title),
                )
            },
            text = {
                Text(
                    text = stringResource(id = content.body),
                )
            }
        )
    }
}

@Composable
fun CustomBottomBar(
    items: Array<BottomBarItem>,
    currentRoute: String,
    onItemClick: (String) -> Unit,
) {
    val contentColor = MaterialTheme.colorScheme.primary
    val unSelectedColor = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(horizontal = 34.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach {
            val isSelected = it.route == currentRoute
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) contentColor.copy(alpha = .1f)
                        else Color.Transparent,
                    )
                    .clickable { onItemClick(it.route) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = it.icon),
                    contentDescription = stringResource(id = it.label),
                    tint = if (isSelected) contentColor else unSelectedColor,
                    modifier = Modifier.size(28.dp),
                )
                AnimatedVisibility(visible = isSelected) {
                    Text(
                        text = stringResource(id = it.label),
                        color = contentColor,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
