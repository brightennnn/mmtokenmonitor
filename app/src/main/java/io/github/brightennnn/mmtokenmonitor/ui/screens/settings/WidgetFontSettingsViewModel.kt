package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepository
import io.github.brightennnn.mmtokenmonitor.domain.model.WidgetFontSizes
import io.github.brightennnn.mmtokenmonitor.widget.TokenWidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WidgetFontSettingsUiState(
    val sizes: WidgetFontSizes = WidgetFontSizes()
)

@HiltViewModel
class WidgetFontSettingsViewModel @Inject constructor(
    private val fontSizeRepository: FontSizeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WidgetFontSettingsUiState())
    val uiState: StateFlow<WidgetFontSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fontSizeRepository.widgetFontSizes.collect { sizes ->
                _uiState.update { it.copy(sizes = sizes) }
            }
        }
    }

    fun updateTitle(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(title = v)) }
    fun updateNumber(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(number = v)) }
    fun updateProgressBar(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(progressBar = v)) }
    fun updatePercent(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(percent = v)) }
    fun updateUpdateTime(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(updateTime = v)) }

    fun save() {
        viewModelScope.launch {
            fontSizeRepository.saveWidgetFontSizes(_uiState.value.sizes)
            // 刷新 Widget 让新字号生效
            TokenWidgetUpdater.refreshWidgets(context, fontSizeRepository)
        }
    }
}
