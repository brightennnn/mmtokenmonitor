package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.brightennnn.mmtokenmonitor.domain.model.WidgetFontSizes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetFontSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: WidgetFontSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget 字体大小") },
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
                text = "Widget 字体大小（5-50）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            FontSlider(
                label = "标题",
                value = uiState.sizes.title,
                onValueChange = { viewModel.updateTitle(it) }
            )
            FontSlider(
                label = "用量数字",
                value = uiState.sizes.number,
                onValueChange = { viewModel.updateNumber(it) }
            )
            FontSlider(
                label = "进度条",
                value = uiState.sizes.progressBar,
                onValueChange = { viewModel.updateProgressBar(it) }
            )
            FontSlider(
                label = "百分比",
                value = uiState.sizes.percent,
                onValueChange = { viewModel.updatePercent(it) }
            )
            FontSlider(
                label = "更新时间",
                value = uiState.sizes.updateTime,
                onValueChange = { viewModel.updateUpdateTime(it) }
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
