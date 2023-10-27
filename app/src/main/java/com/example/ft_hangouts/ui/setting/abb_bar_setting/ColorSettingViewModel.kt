package com.example.ft_hangouts.ui.setting.abb_bar_setting

import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.SharedPreferenceUtils
import kotlinx.coroutines.launch

class ColorSettingViewModel(
    private val preferenceUtils: SharedPreferenceUtils
): ViewModel() {
    @ColorInt
    private var _selectedColor: Int = 0
    val selectedColor get() = _selectedColor

    fun changeColor(color: Int) {
        _selectedColor = color
    }

    fun save() {
        viewModelScope.launch {
            preferenceUtils.setAppbarColor(_selectedColor)
        }
    }
}

class ColorSettingViewModelFactory(
    private val sharedPreferenceUtils: SharedPreferenceUtils
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColorSettingViewModel(sharedPreferenceUtils) as T
    }
}