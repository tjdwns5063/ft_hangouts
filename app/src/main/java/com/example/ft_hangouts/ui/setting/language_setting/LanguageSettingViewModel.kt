package com.example.ft_hangouts.ui.setting.language_setting

import com.example.ft_hangouts.data.SharedPreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageSettingViewModel(
    private val lifecycleScope: CoroutineScope,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    private val _selectedLanguage = MutableStateFlow<String?>(sharedPreferenceUtils.getLanguage())
    val selectedLanguage: StateFlow<String?> = _selectedLanguage.asStateFlow()

    fun updateLanguage(localeString: String?) {
        lifecycleScope.launch {
            sharedPreferenceUtils.setLanguage(localeString)
            _selectedLanguage.value = sharedPreferenceUtils.getLanguage()
        }
    }
}