package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.brightennnn.mmtokenmonitor.domain.model.AppFontSizes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFontSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppFontSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App 字体大小") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
            Text(
                text = "App 页面字体大小（5-50）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            FontSlider(
                label = "模型标题",
                value = uiState.sizes.modelTitle,
                onValueChange = { viewModel.updateModelTitle(it) }
            )
            FontSlider(
                label = "5小时用量标签",
                value = uiState.sizes.intervalLabel,
                onValueChange = { viewModel.updateIntervalLabel(it) }
            )
            FontSlider(
                label = "5小时用量数字",
                value = uiState.sizes.intervalNumber,
                onValueChange = { viewModel.updateIntervalNumber(it) }
            )
            FontSlider(
                label = "5小时百分比",
                value = uiState.sizes.intervalPercent,
                onValueChange = { viewModel.updateIntervalPercent(it) }
            )
            FontSlider(
                label = "本周用量标签",
                value = uiState.sizes.weeklyLabel,
                onValueChange = { viewModel.updateWeeklyLabel(it) }
            )
            FontSlider(
                label = "本周用量数字",
                value = uiState.sizes.weeklyNumber,
                onValueChange = { viewModel.updateWeeklyNumber(it) }
            )
            FontSlider(
                label = "本周百分比",
                value = uiState.sizes.weeklyPercent,
                onValueChange = { viewModel.updateWeeklyPercent(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}

@Composable
private fun FontSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${value}sp", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 5f..50f,
            steps = 0,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
