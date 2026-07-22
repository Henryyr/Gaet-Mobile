package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ButtonStyle {
    Primary, Outline
}

/**
 * A highly reusable, themed button with loading and icon support.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null
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
            ButtonContent(text, isLoading, icon)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = buttonColors,
            border = ButtonDefaults.outlinedButtonBorder(enabled = enabled && !isLoading)
        ) {
            ButtonContent(text, isLoading, icon)
        }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean, icon: ImageVector?) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalContentColor.current,
            strokeWidth = 2.dp
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
            if (icon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}
