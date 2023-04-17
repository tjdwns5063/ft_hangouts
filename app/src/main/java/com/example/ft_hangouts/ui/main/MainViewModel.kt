package com.example.ft_hangouts.ui.main

import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO

class MainViewModel {
    private val contactDatabaseDAO: ContactDatabaseDAO = ContactDatabaseDAO()

    fun getAllContact(): List<Contact> {
        return contactDatabaseDAO.getAllItems()
    }

    fun getAppbarColor(): Int {
        return SharedPreferenceUtils.getAppbarColor()
    }

    fun closeDatabase() {
        contactDatabaseDAO.closeDatabase()
    }
}