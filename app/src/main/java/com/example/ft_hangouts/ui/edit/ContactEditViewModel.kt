package com.example.ft_hangouts.ui.edit

import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler

class ContactEditViewModel(private val handler: Handler) {
    private val databaseDAO = ContactDatabaseDAO()

    val errorHandler: LiveData<DatabaseErrorHandler>
        get() = _errorHandler
    private val _errorHandler = MutableLiveData<DatabaseErrorHandler>()

    fun updateContactById(rowId: Long, newContact: Contact) {
        BackgroundHelper.execute {
            try {
                databaseDAO.updateById(rowId, newContact)
            } catch (err: Exception) {
                handler.post { _errorHandler.value = DatabaseUpdateErrorHandler() }
            } finally {
                handler.post { _errorHandler.value = null }
            }
        }
    }
}