package com.example.ft_hangouts.ui.detail

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseDeleteErrorHandler
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler

class ContactDetailViewModel(private val handler: Handler, id: Long) {
    private val contactDatabaseDAO = ContactDatabaseDAO()

    val contact: LiveData<Contact>
        get() = _contact
    private val _contact = MutableLiveData<Contact>()

    val errorHandler: LiveData<DatabaseErrorHandler>
        get() = _errorHandler
    private val _errorHandler = MutableLiveData<DatabaseErrorHandler>()

    init {
        getContactById(id)
    }

    private fun getContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                val contact = contactDatabaseDAO.getItemById(id)

                handler.post { _contact.value = contact }
                println(contact)
            } catch (err: Exception) {
                handler.post { _errorHandler.value = DatabaseReadErrorHandler() }
            } finally {
                handler.post { _errorHandler.value = null }
            }
        }
    }

    fun deleteContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                contactDatabaseDAO.deleteById(id)
            } catch (err: Exception) {
                handler.post { _errorHandler.value = DatabaseDeleteErrorHandler() }
            } finally {
                handler.post { _errorHandler.value = null }
            }
        }
    }
}