package com.example.ft_hangouts.ui.main

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel
import com.example.ft_hangouts.ui.setting.abb_bar_setting.AppBarSettingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
    ) {
    private val contactDatabaseDAO: ContactDatabaseDAO = ContactDatabaseDAO()

    val contactList: StateFlow<List<ContactDomainModel>>
        get() = _contactList
    private val _contactList = MutableStateFlow<List<ContactDomainModel>>(emptyList())

    val appBarColor: StateFlow<Int>
        get() = _appBarColor
    private val _appBarColor = MutableStateFlow<Int>(16119285)

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
        _appBarColor.value = SharedPreferenceUtils.getAppbarColor()
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