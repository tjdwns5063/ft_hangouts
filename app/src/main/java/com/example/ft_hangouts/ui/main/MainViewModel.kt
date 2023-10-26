package com.example.ft_hangouts.ui.main

import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.system.CallSystemHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val contactDAO: ContactDAO,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
    ) {
    private val _contactList = MutableStateFlow<List<ContactDomainModel>>(emptyList())
    val contactList: StateFlow<List<ContactDomainModel>> = _contactList.asStateFlow()

    private val _appBarColor = MutableStateFlow<Int>(-657931)
    val appBarColor: StateFlow<Int> = _appBarColor.asStateFlow()

    init {
        lifecycleScope.launch {
            initRecyclerList()
            updateAppbarColor()
        }
    }

    private suspend fun getContactList() = withContext(Dispatchers.IO) {
        try {
            _contactList.value = contactDAO.getAllItems().map { contactToContactDomainModel(it) }
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    fun initRecyclerList() = lifecycleScope.launch {
        getContactList()
    }

    private suspend fun getAppbarColor() = withContext(Dispatchers.IO) {
        _appBarColor.value = sharedPreferenceUtils.getAppbarColor()
    }

    fun updateAppbarColor() = lifecycleScope.launch {
        getAppbarColor()
    }
}