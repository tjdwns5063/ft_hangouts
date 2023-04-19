package com.example.ft_hangouts.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.error.DatabaseHandler

class BaseViewModel {
    val errorHandler: LiveData<DatabaseHandler>
        get() = _errorHandler
    private val _errorHandler = MutableLiveData<DatabaseHandler>()

    fun submitHandler(handler:DatabaseHandler?) {
        _errorHandler.postValue(handler)
    }
}