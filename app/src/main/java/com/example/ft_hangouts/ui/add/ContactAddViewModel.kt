package com.example.ft_hangouts.ui.add

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseDeleteErrorHandler
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.BaseViewModel

class ContactAddViewModel(private val handler: Handler, private val baseViewModel: BaseViewModel) {
    private val contactDAO = ContactDatabaseDAO()

    fun addContact(contact: Contact) {
        BackgroundHelper.execute {
            try {
                contactDAO.addItem(contact)
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseDeleteErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(DatabaseSuccessHandler()) }
            }
        }
    }
}