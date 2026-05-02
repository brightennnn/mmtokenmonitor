package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.brightennnn.mmtokenmonitor.data.repository.ThemeRepository
import io.github.brightennnn.mmtokenmonitor.domain.model.ContrastLevel
import io.github.brightennnn.mmtokenmonitor.domain.model.DarkMode
import io.github.brightennnn.mmtokenmonitor.domain.model.PaletteStyle
import io.github.brightennnn.mmtokenmonitor.domain.model.ThemeSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeRepository: ThemeRepository,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeSettings by themeRepository.themeSettings.collectAsState(initial = ThemeSettings())
    var passwordVisible by remember { mutableStateOf(false) }

    // Dialog states
    var showDarkModeDialog by remember { mutableStateOf(false) }
    var showPaletteStyleDialog by remember { mutableStateOf(false) }
    var showContrastLevelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // API Key 设置
            Text(
                text = "API Key",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.hasKey && !uiState.isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "已设置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        TextButton(onClick = { viewModel.startEditing() }) {
                            Text("修改")
                        }
                        TextButton(onClick = { viewModel.clearKey() }) {
                            Text("清除")
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = uiState.apiKey,
                    onValueChange = { viewModel.updateApiKey(it) },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.saveAndFetch() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "隐藏" else "显示"
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (uiState.hasKey) {
                        TextButton(onClick = { viewModel.cancelEditing() }) {
                            Text("取消")
                        }
                    }
                    Button(
                        onClick = { viewModel.saveAndFetch() },
                        enabled = uiState.apiKey.isNotBlank() && !uiState.isLoading
                    ) {
                        Text("保存")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // MD3E 主题设置
            Text(
                text = "主题",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // DynamicColor 开关
            ListItem(
                headlineContent = { Text("动态颜色") },
                supportingContent = { Text("跟随壁纸颜色（Material You）") },
                trailingContent = {
                    Switch(
                        checked = themeSettings.dynamicColor,
                        onCheckedChange = { viewModel.setDynamicColor(it) }
                    )
                }
            )
            HorizontalDivider()

            // Dark Mode
            ListItem(
                headlineContent = { Text("深色模式") },
                supportingContent = { Text(darkModeLabel(themeSettings.darkMode)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable { showDarkModeDialog = true }
            )
            HorizontalDivider()

            // Palette Style
            ListItem(
                headlineContent = { Text("调色板风格") },
                supportingContent = { Text(themeSettings.paletteStyle.displayName) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable { showPaletteStyleDialog = true }
            )
            HorizontalDivider()

            // Contrast Level
            ListItem(
                headlineContent = { Text("对比度级别") },
                supportingContent = { Text(themeSettings.contrastLevel.name) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable { showContrastLevelDialog = true }
            )
            HorizontalDivider()

            // PureBlack 开关
            ListItem(
                headlineContent = { Text("纯黑深色模式") },
                supportingContent = { Text("深色时使用纯黑背景（节省 OLED 电量）") },
                trailingContent = {
                    Switch(
                        checked = themeSettings.pureBlack,
                        onCheckedChange = { viewModel.setPureBlack(it) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // 关于
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MiniMax Token Monitor",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "版本 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "查询 MiniMax API Key 的 token 额度使用情况",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Dark Mode Dialog
    if (showDarkModeDialog) {
        AlertDialog(
            onDismissRequest = { showDarkModeDialog = false },
            title = { Text("深色模式") },
            text = {
                Column {
                    DarkMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDarkMode(mode)
                                    showDarkModeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(darkModeLabel(mode))
                            RadioButton(
                                selected = themeSettings.darkMode == mode,
                                onClick = {
                                    viewModel.setDarkMode(mode)
                                    showDarkModeDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Palette Style Dialog
    if (showPaletteStyleDialog) {
        AlertDialog(
            onDismissRequest = { showPaletteStyleDialog = false },
            title = { Text("调色板风格") },
            text = {
                Column {
                    PaletteStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setPaletteStyle(style)
                                    showPaletteStyleDialog = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(style.displayName)
                            RadioButton(
                                selected = themeSettings.paletteStyle == style,
                                onClick = {
                                    viewModel.setPaletteStyle(style)
                                    showPaletteStyleDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Contrast Level Dialog
    if (showContrastLevelDialog) {
        AlertDialog(
            onDismissRequest = { showContrastLevelDialog = false },
            title = { Text("对比度级别") },
            text = {
                Column {
                    ContrastLevel.entries.forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setContrastLevel(level)
                                    showContrastLevelDialog = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(level.name)
                            RadioButton(
                                selected = themeSettings.contrastLevel == level,
                                onClick = {
                                    viewModel.setContrastLevel(level)
                                    showContrastLevelDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

private fun darkModeLabel(mode: DarkMode): String = when (mode) {
    DarkMode.FOLLOW_SYSTEM -> "跟随系统"
    DarkMode.LIGHT -> "浅色"
    DarkMode.DARK -> "深色"
}
