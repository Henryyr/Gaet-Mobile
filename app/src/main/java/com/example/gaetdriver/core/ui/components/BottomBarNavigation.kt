package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gaetdriver.constant.AppNavDestinations
import androidx.window.core.layout.WindowSizeClass
import com.example.gaetdriver.core.base.i18n.LocalStrings

@Composable
fun BottomBarNavigation(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    onAddClick: () -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val strings = LocalStrings.current

    if (isExpanded) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp)),
            header = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(AppNavDestinations.ADD.icon), null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        ) {
            AppNavDestinations.entries.filter { it != AppNavDestinations.ADD }.forEach { destination ->
                val isActive = currentDestination == destination.route
                val label = when(destination) {
                    AppNavDestinations.HOME -> strings.home
                    AppNavDestinations.ACTIVITY -> strings.activity
                    AppNavDestinations.LIBRARY -> strings.library
                    AppNavDestinations.PROFILE -> strings.profile
                    else -> strings.home
                }

                NavigationRailItem(
                    selected = isActive,
                    onClick = { navController.navigate(destination.route) },
                    icon = { Icon(painterResource(destination.icon), null) },
                    label = { Text(label) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    } else {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppNavDestinations.entries.forEach { destination ->
                    val isAdd = destination == AppNavDestinations.ADD
                    val isActive = currentDestination == destination.route

                    Box(
                        modifier = Modifier
                            .size(if (isAdd) 70.dp else 60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isAdd) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                if (isAdd) onAddClick() else navController.navigate(destination.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(destination.icon),
                            contentDescription = destination.route,
                            modifier = Modifier.size(30.dp),
                            tint = when {
                                isAdd -> MaterialTheme.colorScheme.onPrimary
                                isActive -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}
