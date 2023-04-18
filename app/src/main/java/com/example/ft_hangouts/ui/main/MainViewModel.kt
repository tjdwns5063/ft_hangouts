package com.example.ft_hangouts.ui.main

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel
import com.example.ft_hangouts.ui.setting.abb_bar_setting.AppBarSettingActivity


class MainViewModel(private val handler: Handler, private val baseViewModel: BaseViewModel) {
    private val contactDatabaseDAO: ContactDatabaseDAO = ContactDatabaseDAO()

    val contactList: LiveData<List<Contact>>
        get() = _contactList
    private val _contactList = MutableLiveData<List<Contact>>()

    val appBarColor: LiveData<Int>
        get() = _appBarColor
    private val _appBarColor = MutableLiveData<Int>()

    init {
        initRecyclerList()
        updateAppbarColor()
    }

    private fun getContactList() {
        BackgroundHelper.execute {
            try {
                handler.post { _contactList.value = contactDatabaseDAO.getAllItems() }
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseReadErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(null) }
            }
        }
    }

    fun initRecyclerList() {
        getContactList()
    }

    private fun getAppbarColor() {
        BackgroundHelper.execute {
            handler.post { _appBarColor.value = SharedPreferenceUtils.getAppbarColor() }
        }
    }

    fun updateAppbarColor() {
        getAppbarColor()
    }

    fun closeDatabase() {
        contactDatabaseDAO.closeDatabase()
    }
}