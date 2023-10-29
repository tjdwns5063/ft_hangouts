package com.example.ft_hangouts.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val contactDAO: ContactDAO,
    private val baseViewModel: BaseViewModel
    ): ViewModel() {
    private val _contactList = MutableStateFlow<List<Contact>>(emptyList())
    val contactList: StateFlow<List<Contact>> = _contactList.asStateFlow()

    private val _appBarColor = MutableStateFlow<Int>(-657931)
    val appBarColor: StateFlow<Int> = _appBarColor.asStateFlow()

    init {
        viewModelScope.launch {
            initRecyclerList()
            updateAppbarColor()
        }
    }

    private suspend fun getContactList() = withContext(Dispatchers.IO) {
        try {
            _contactList.value = contactDAO.getAllItems()
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    suspend fun initRecyclerList() {
        getContactList()
    }

    private suspend fun getAppbarColor() = withContext(Dispatchers.IO) {
        _appBarColor.value = sharedPreferenceUtils.getAppbarColor()
    }

    suspend fun updateAppbarColor() {
        getAppbarColor()
    }
}

class MainViewModelFactory(
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val baseViewModel: BaseViewModel,
    private val database: ContactDatabase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            sharedPreferenceUtils,
            database.contactDao(),
            baseViewModel
        ) as T
    }
}