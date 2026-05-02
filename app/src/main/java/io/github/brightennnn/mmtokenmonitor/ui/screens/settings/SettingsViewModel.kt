package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepository
import io.github.brightennnn.mmtokenmonitor.data.repository.ThemeRepository
import io.github.brightennnn.mmtokenmonitor.domain.model.ContrastLevel
import io.github.brightennnn.mmtokenmonitor.domain.model.DarkMode
import io.github.brightennnn.mmtokenmonitor.domain.model.PaletteStyle
import io.github.brightennnn.mmtokenmonitor.domain.repository.TokenRepository
import io.github.brightennnn.mmtokenmonitor.widget.TokenWidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKey: String = "",
    val hasKey: Boolean = false,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TokenRepository,
    private val fontSizeRepository: FontSizeRepository,
    private val themeRepository: ThemeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSavedKey()
    }

    private fun loadSavedKey() {
        viewModelScope.launch {
            repository.apiKey.collect { key ->
                _uiState.update { it.copy(apiKey = key ?: "", hasKey = !key.isNullOrBlank(), isEditing = false) }
            }
        }
    }

    fun updateApiKey(key: String) {
        _uiState.update { it.copy(apiKey = key, error = null) }
    }

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true, apiKey = it.apiKey) }
    }

    fun cancelEditing() {
        viewModelScope.launch {
            val key = repository.apiKey.first()
            _uiState.update { it.copy(isEditing = false, apiKey = key ?: "") }
        }
    }

    fun saveAndFetch() {
        val key = _uiState.value.apiKey.trim()
        if (key.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.saveApiKey(key)
            _uiState.update { it.copy(hasKey = true, isEditing = false, isLoading = false) }
            // Fetch and update widget
            repository.fetchQuotas(key)
                .onSuccess { quotas ->
                    TokenWidgetUpdater.updateWidget(context, quotas, fontSizeRepository)
                }
                .onFailure { _ ->
                    // Silent fail, user can retry
                }
        }
    }

    fun clearKey() {
        viewModelScope.launch {
            repository.clearApiKey()
            _uiState.update { it.copy(apiKey = "", hasKey = false, isEditing = false) }
        }
    }

    // MD3E Theme Settings
    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.saveDynamicColor(enabled)
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }

    fun setDarkMode(mode: DarkMode) {
        viewModelScope.launch {
            themeRepository.saveDarkMode(mode)
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }

    fun setPaletteStyle(style: PaletteStyle) {
        viewModelScope.launch {
            themeRepository.savePaletteStyle(style)
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }

    fun setContrastLevel(level: ContrastLevel) {
        viewModelScope.launch {
            themeRepository.saveContrastLevel(level)
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }

    fun setPureBlack(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.savePureBlack(enabled)
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }
}
