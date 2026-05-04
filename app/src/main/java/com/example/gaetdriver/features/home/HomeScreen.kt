package com.example.gaetdriver.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.constant.AppConfig
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

@Composable
fun HomeScreen() {
    val strings = LocalStrings.current
    val env = AppConfig.environmentName
    val baseUrl = AppConfig.baseUrl
    val isDev = AppConfig.isDev

    ViewLayout(
        header = {
            SectionHeader(
                title = strings.home,
                actionLabel = "Env: $env"
            )
        },
        body = {

                    EmptyState(
                        message = strings.home,
                        description = strings.homeDescription
                    )


                if (isDev) {

                    Column(verticalArrangement = Arrangement.SpaceEvenly) {
                        Text(
                            text = "Developer Info (Dev Mode)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Base URL: $baseUrl",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    }


        }
    )
}
