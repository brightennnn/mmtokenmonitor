package io.github.brightennnn.mmtokenmonitor.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepository
import io.github.brightennnn.mmtokenmonitor.domain.model.AppFontSizes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppFontSettingsUiState(
    val sizes: AppFontSizes = AppFontSizes()
)

@HiltViewModel
class AppFontSettingsViewModel @Inject constructor(
    private val fontSizeRepository: FontSizeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppFontSettingsUiState())
    val uiState: StateFlow<AppFontSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fontSizeRepository.appFontSizes.collect { sizes ->
                _uiState.update { it.copy(sizes = sizes) }
            }
        }
    }

    fun updateModelTitle(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(modelTitle = v)) }
    fun updateIntervalLabel(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(intervalLabel = v)) }
    fun updateIntervalNumber(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(intervalNumber = v)) }
    fun updateIntervalPercent(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(intervalPercent = v)) }
    fun updateWeeklyLabel(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(weeklyLabel = v)) }
    fun updateWeeklyNumber(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(weeklyNumber = v)) }
    fun updateWeeklyPercent(v: Int) = _uiState.update { it.copy(sizes = it.sizes.copy(weeklyPercent = v)) }

    fun save() {
        viewModelScope.launch {
            fontSizeRepository.saveAppFontSizes(_uiState.value.sizes)
        }
    }
}
