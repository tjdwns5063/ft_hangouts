package com.example.ft_hangouts.ui.add

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseDeleteErrorHandler
import com.example.ft_hangouts.error.DatabaseErrorHandler

class ContactAddViewModel(private val handler: Handler) {
    private val contactDAO = ContactDatabaseDAO()

    val errorHandler: LiveData<DatabaseErrorHandler>
        get() = _errorHandler
    private val _errorHandler = MutableLiveData<DatabaseErrorHandler>()

    fun addContact(contact: Contact) {
        BackgroundHelper.execute {
            try {
                contactDAO.addItem(contact)
            } catch (err: Exception) {
                handler.post { _errorHandler.value = DatabaseDeleteErrorHandler() }
            } finally {
                handler.post { _errorHandler.value = null }
            }
        }
    }
}