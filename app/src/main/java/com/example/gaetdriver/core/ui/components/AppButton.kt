package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ButtonStyle {
    Primary, Outline
}

/**
 * A highly reusable, themed button with loading support.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val buttonColors = when (style) {
        ButtonStyle.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        ButtonStyle.Outline -> ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    }

    if (style == ButtonStyle.Primary) {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = buttonColors
        ) {
            ButtonContent(text, isLoading)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = buttonColors,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        ) {
            ButtonContent(text, isLoading)
        }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalContentColor.current,
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
