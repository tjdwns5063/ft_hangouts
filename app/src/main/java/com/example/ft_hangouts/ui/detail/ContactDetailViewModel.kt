package com.example.ft_hangouts.ui.detail

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.*
import com.example.ft_hangouts.ui.BaseViewModel

class ContactDetailViewModel(private val handler: Handler, id: Long, private val baseViewModel: BaseViewModel) {
    private val contactDatabaseDAO = ContactDatabaseDAO()

    val contact: LiveData<Contact>
        get() = _contact
    private val _contact = MutableLiveData<Contact>()

    init {
        getContactById(id)
    }

    private fun getContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                val contact = contactDatabaseDAO.getItemById(id)

                handler.post { _contact.value = contact }
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseReadErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(null) }
            }
        }
    }

    fun deleteContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                contactDatabaseDAO.deleteById(id)
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseDeleteErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(DatabaseSuccessHandler()) }
            }
        }
    }

    fun updateContact(id: Long) {
        getContactById(id)
    }
}