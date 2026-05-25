package com.example.gaetdriver.features.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.ActivityLog
import com.example.gaetdriver.core.ui.components.AppCard
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import androidx.compose.ui.platform.LocalConfiguration
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivityScreen() {
    val strings = LocalStrings.current
    val authManager = rememberAuthManager()
    val portfolioRepo = rememberPortfolioRepository()
    
    val userId = authManager.currentUserId
    val logs by portfolioRepo.getActivityLogs(userId ?: "").collectAsState(initial = null)

    ViewLayout(
        header = {
            SectionHeader(title = strings.activity)
        },
        body = {
            if (logs == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else if (logs!!.isEmpty()) {
                EmptyState(
                    message = strings.noActivity,
                    description = strings.noActivityDescription
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(logs!!) { log ->
                        ActivityItem(log = log)
                    }
                }
            }
        }
    )
}

@Composable
fun ActivityItem(log: ActivityLog) {
    val locale = LocalConfiguration.current.locales[0]
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", locale)
    val dateString = sdf.format(Date(log.createdAt))

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = log.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = log.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
