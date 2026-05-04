package com.example.gaetdriver.features.profile
import androidx.compose.runtime.Composable
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import com.example.gaetdriver.features.profile.ui.BodyContent

@Composable
fun ProfileScreen(authManager: AuthManager) {
    val strings = LocalStrings.current

    ViewLayout(
        header = {
            SectionHeader(title = strings.profileSettings)
        },
        body = {

                BodyContent(
                    authManager = authManager,
                )
            }

    )
}

