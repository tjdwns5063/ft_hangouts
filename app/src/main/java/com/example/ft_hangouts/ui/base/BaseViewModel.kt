package com.example.ft_hangouts.ui.base

import com.example.ft_hangouts.error.DatabaseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseViewModel(
    private val lifecycleScope: CoroutineScope
    ) {
    private val _errorHandler = MutableStateFlow<DatabaseHandler?>(null)
    val errorHandler: StateFlow<DatabaseHandler?> = _errorHandler.asStateFlow()

    fun submitHandler(handler:DatabaseHandler?) {
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