package com.example.ft_hangouts.ui.setting.language_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageSettingViewModel(
    private val sharedPreferenceUtils: SharedPreferenceUtils
): ViewModel() {
    private val _selectedLanguage = MutableStateFlow<String?>(sharedPreferenceUtils.getLanguage())
    val selectedLanguage: StateFlow<String?> = _selectedLanguage.asStateFlow()

    fun updateLanguage(localeString: String?)  {
        viewModelScope.launch {
            sharedPreferenceUtils.setLanguage(localeString)
            _selectedLanguage.value = sharedPreferenceUtils.getLanguage()
        }
    }
}

class LanguageViewModelFactory(
    private val sharedPreferenceUtils: SharedPreferenceUtils
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LanguageSettingViewModel(
            sharedPreferenceUtils
        ) as T
    }
}