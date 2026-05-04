package com.example.gaetdriver.features.activity

import androidx.compose.runtime.Composable
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

@Composable
fun ActivityScreen() {
    val strings = LocalStrings.current
    ViewLayout(
        header = {
            SectionHeader(title = strings.activity)
        },
        body = {

                EmptyState(
                    message = strings.noActivity,
                    description = strings.noActivityDescription
                )

        }
    )
}
