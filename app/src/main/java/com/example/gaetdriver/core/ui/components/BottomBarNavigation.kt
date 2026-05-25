package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.constant.AppNavDestinations
import androidx.window.core.layout.WindowSizeClass
import com.example.gaetdriver.core.base.i18n.LocalStrings
import kotlinx.coroutines.launch

@Composable
fun BottomBarNavigation(
    pagerState: PagerState,
    windowSizeClass: WindowSizeClass,
    onAddClick: () -> Unit
) {
    val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    
    // Mapping pages to routes for the pager indices
    val tabs = remember {
        listOf(
            AppNavDestinations.HOME,
            AppNavDestinations.ACTIVITY,
            AppNavDestinations.LIBRARY,
            AppNavDestinations.PROFILE
        )
    }

    if (isExpanded) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(),
            header = {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(AppNavDestinations.ADD.icon),
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, destination ->
                val isActive = pagerState.currentPage == index
                val label = when(destination) {
                    AppNavDestinations.HOME -> strings.home
                    AppNavDestinations.ACTIVITY -> strings.activity
                    AppNavDestinations.LIBRARY -> strings.library
                    AppNavDestinations.PROFILE -> strings.profile
                    else -> ""
                }

                NavigationRailItem(
                    selected = isActive,
                    onClick = { scope.launch { pagerState.scrollToPage(index) } },
                    icon = { Icon(painterResource(destination.icon), null) },
                    label = { Text(label) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    } else {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logic to insert ADD button in the middle
                    val allDestinations = listOf(
                        tabs[0], tabs[1], AppNavDestinations.ADD, tabs[2], tabs[3]
                    )

                    allDestinations.forEach { destination ->
                        val isAdd = destination == AppNavDestinations.ADD
                        val tabIndex = tabs.indexOf(destination)
                        val isActive = !isAdd && pagerState.currentPage == tabIndex

                        Box(
                            modifier = Modifier
                                .size(if (isAdd) 64.dp else 50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    when {
                                        isAdd -> MaterialTheme.colorScheme.primary
                                        isActive -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    }
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (isAdd) onAddClick() 
                                    else if (tabIndex != -1) {
                                        scope.launch { pagerState.scrollToPage(tabIndex) }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.route,
                                modifier = Modifier.size(if (isAdd) 28.dp else 24.dp),
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
}
