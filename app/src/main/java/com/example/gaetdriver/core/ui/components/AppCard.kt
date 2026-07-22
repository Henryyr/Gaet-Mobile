package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.constant.Variant


/**
 * A reusable, themed Card component.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: Variant = Variant.Flat,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier
    }

    when (variant) {
        Variant.Elevated -> {
            Card(
                modifier = cardModifier,
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                content = {
                    Column(modifier = Modifier.padding(contentPadding)) {
                        content()
                    }
                }
            )
        }
        Variant.Outlined -> {
            Card(
                modifier = cardModifier,
                shape = shape,
                border = border ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                content = {
                    Column(modifier = Modifier.padding(contentPadding)) {
                        content()
                    }
                }
            )
        }
        Variant.Flat -> {
            Card(
                modifier = cardModifier,
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = containerColor),
                content = {
                    Column(modifier = Modifier.padding(contentPadding)) {
                        content()
                    }
                }
            )
        }
    }
}
