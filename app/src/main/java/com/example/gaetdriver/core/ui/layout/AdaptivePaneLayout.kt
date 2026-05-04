package com.example.gaetdriver.core.ui.layout

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

/**
 * A List-Detail pattern layout that automatically adapts to screen size.
 * Uses Material 3 Adaptive Scaffold.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T : Any> AdaptivePaneLayout(
    listContent: @Composable (onClick: (T) -> Unit) -> Unit,
    detailContent: @Composable (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<T>()
    val scope = rememberCoroutineScope()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            listContent { item ->
                scope.launch {
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                }
            }
        },
        detailPane = {
            detailContent(navigator.currentDestination?.contentKey)
        },
        modifier = modifier
    )
}
