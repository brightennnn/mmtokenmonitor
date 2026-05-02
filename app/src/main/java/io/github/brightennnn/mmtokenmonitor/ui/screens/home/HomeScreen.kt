package io.github.brightennnn.mmtokenmonitor.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepository
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepositoryEntryPoint
import io.github.brightennnn.mmtokenmonitor.domain.model.AppFontSizes
import io.github.brightennnn.mmtokenmonitor.domain.model.ModelQuota
import dagger.hilt.android.EntryPointAccessors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MiniMax Token Monitor") },
                actions = {
                    if (uiState.hasKey) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "刷新")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 错误信息
            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 无 Key 时提示
            if (!uiState.hasKey) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "请先设置 API Key",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "点击右上角设置按钮添加",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // 加载状态
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 模型列表（过滤掉 0/0 的 和 coding-plan）
            val visibleQuotas = uiState.quotas.filter {
                val hasData = it.intervalUsed > 0 || it.intervalTotal > 0 || it.weeklyUsed > 0 || it.weeklyTotal > 0
                val notCodingPlan = !it.modelName.contains("coding-plan", ignoreCase = true)
                hasData && notCodingPlan
            }
            if (visibleQuotas.isNotEmpty()) {
                Text(
                    text = "模型用量",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visibleQuotas) { quota ->
                        QuotaCard(quota = quota)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuotaCard(quota: ModelQuota) {
    val context = LocalContext.current
    val fontSizeRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            FontSizeRepositoryEntryPoint::class.java
        ).fontSizeRepository()
    }
    val fontSizes by fontSizeRepository.appFontSizes.collectAsState(initial = AppFontSizes())

    val beijingZone = ZoneId.of("Asia/Shanghai")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("MM-dd")

    val intervalStart = Instant.ofEpochMilli(quota.intervalStartMs).atZone(beijingZone)
    val intervalEnd = Instant.ofEpochMilli(quota.intervalEndMs).atZone(beijingZone)
    val weeklyStart = Instant.ofEpochMilli(quota.weeklyStartMs).atZone(beijingZone)
    val weeklyEnd = Instant.ofEpochMilli(quota.weeklyEndMs).atZone(beijingZone)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = displayModelName(quota.modelName),
                fontSize = fontSizes.modelTitle.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5小时用量：label+时间段居左，用量居右
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5小时用量（${intervalStart.format(timeFormatter)} - ${intervalEnd.format(timeFormatter)}）",
                    fontSize = fontSizes.intervalLabel.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${quota.intervalUsed} / ${quota.intervalTotal}",
                    fontSize = fontSizes.intervalNumber.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (quota.intervalUsed > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 进度条 + 百分比
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val intervalFraction = if (quota.intervalTotal > 0) quota.intervalUsed.toFloat() / quota.intervalTotal.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { intervalFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = if (intervalFraction > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(intervalFraction * 100).toInt().toString().padStart(3)}%",
                    fontSize = fontSizes.intervalPercent.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // 本周用量：label+日期居左，用量居右
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "本周用量（${weeklyStart.format(dateFormatter)} ~ ${weeklyEnd.format(dateFormatter)}）",
                    fontSize = fontSizes.weeklyLabel.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${quota.weeklyUsed} / ${quota.weeklyTotal}",
                    fontSize = fontSizes.weeklyNumber.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (quota.weeklyUsed > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 进度条 + 百分比
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val weeklyFraction = if (quota.weeklyTotal > 0) quota.weeklyUsed.toFloat() / quota.weeklyTotal.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { weeklyFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = if (weeklyFraction > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(weeklyFraction * 100).toInt().toString().padStart(3)}%",
                    fontSize = fontSizes.weeklyPercent.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun displayModelName(modelName: String): String = modelName
