package com.example.gaetdriver.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BodyContent() {
    Column {
        Section(icon = Icons.Default.Computer,"Agent") {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Color.White
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ) {
                        print("test")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.ProductionQuantityLimits, contentDescription = null, Modifier.size(40.dp))
            }
        }
        Section(icon = Icons.Default.Bolt,"Automations") {
            Text("List Of Agents")
        }
        Section(icon = Icons.Default.ChatBubble,"Active Chats") {
            Text("List Of Agents")
        }
        Section(icon = Icons.Default.DataUsage,"Providers") {
            Text("List Of Agents")
        }
    }
}