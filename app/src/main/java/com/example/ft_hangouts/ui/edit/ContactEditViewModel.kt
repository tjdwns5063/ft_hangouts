package com.example.ft_hangouts.ui.edit

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel

class ContactEditViewModel(
    id: Long,
    private val handler: Handler,
    private val baseViewModel: BaseViewModel
    ) {
    private val contactDatabaseDAO = ContactDatabaseDAO()

    val contact: LiveData<ContactDomainModel>
        get() = _contact
    private val _contact = MutableLiveData<ContactDomainModel>()

    init {
        getContactById(id)
    }

    private fun getContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                val contact = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))

                handler.post { _contact.value = contact }
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseReadErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(null) }
            }
        }
    }

    fun updateContactById(rowId: Long, newContact: Contact) {
        BackgroundHelper.execute {
            try {
                contactDatabaseDAO.updateById(rowId, newContact)
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseUpdateErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(DatabaseSuccessHandler()) }
            }
        }
    }
}