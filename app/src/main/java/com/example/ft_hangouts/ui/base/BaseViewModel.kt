package com.example.ft_hangouts.ui.base

import com.example.ft_hangouts.error.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseViewModel(
    private val lifecycleScope: CoroutineScope
    ) {
    private val _errorHandler = MutableStateFlow<ErrorHandler?>(null)
    val errorHandler: StateFlow<ErrorHandler?> = _errorHandler.asStateFlow()

    fun submitHandler(handler:ErrorHandler?) {
        lifecycleScope.launch {
            _errorHandler.value = handler
        }
    }

    fun initiateError() {
        lifecycleScope.launch {
            _errorHandler.value = null
        }
    }
}