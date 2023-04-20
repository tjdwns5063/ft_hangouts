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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(private val handler: Handler, private val baseViewModel: BaseViewModel) {
    private val contactDatabaseDAO: ContactDatabaseDAO = ContactDatabaseDAO()

    val contactList: LiveData<List<ContactDomainModel>>
        get() = _contactList
    private val _contactList = MutableLiveData<List<ContactDomainModel>>()

    val appBarColor: LiveData<Int>
        get() = _appBarColor
    private val _appBarColor = MutableLiveData<Int>()

    init {
        initRecyclerList()
        updateAppbarColor()
    }

    private suspend fun getContactList() = withContext(Dispatchers.IO) {
        try {
            val lst = contactDatabaseDAO.getAllItems().map { contactToContactDomainModel(it) }
            _contactList.postValue(lst)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    fun initRecyclerList() {
        CoroutineScope(Dispatchers.IO).launch {
            getContactList()
        }
    }

    private suspend fun getAppbarColor() = withContext(Dispatchers.IO) {
        _appBarColor.postValue(SharedPreferenceUtils.getAppbarColor())
    }

    fun updateAppbarColor() {
        CoroutineScope(Dispatchers.IO).launch {
            getAppbarColor()
        }
    }

    fun closeDatabase() {
        contactDatabaseDAO.closeDatabase()
    }
}