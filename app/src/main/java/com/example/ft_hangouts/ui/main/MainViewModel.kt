package com.example.ft_hangouts.ui.main

import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
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
    contactDAO: ContactDatabaseDAO,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
    ) {
    private val contactDatabaseDAO: ContactDatabaseDAO = contactDAO

    private val _contactList = MutableStateFlow<List<ContactDomainModel>>(emptyList())
    val contactList: StateFlow<List<ContactDomainModel>> = _contactList.asStateFlow()

    private val _appBarColor = MutableStateFlow<Int>(16119285)
    val appBarColor: StateFlow<Int> = _appBarColor.asStateFlow()

    init {
        initRecyclerList()
        updateAppbarColor()
    }

    private suspend fun getContactList() = withContext(Dispatchers.IO) {
        try {
            val lst = contactDatabaseDAO.getAllItems().map { contactToContactDomainModel(it) }
            _contactList.value = lst
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    fun initRecyclerList() {
        lifecycleScope.launch {
            getContactList()
        }
    }

    private suspend fun getAppbarColor() = withContext(Dispatchers.IO) {
        _appBarColor.value = sharedPreferenceUtils.getAppbarColor()
    }

    fun updateAppbarColor() {
        lifecycleScope.launch {
            getAppbarColor()
        }
    }

    fun closeDatabase() {
        contactDatabaseDAO.closeDatabase()
    }
}