package com.example.ft_hangouts.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.telecom.TelecomManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.ft_hangouts.App
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.error.*
import com.example.ft_hangouts.ui.BaseViewModel

class ContactDetailViewModel(private val handler: Handler, private val id: Long, private val baseViewModel: BaseViewModel) {
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

    fun updateContact() {
        getContactById(id)
    }

    fun call(telecomManager: TelecomManager, checkPermission: (String) -> Int) {
        contact.value?.let {
            val callUri = Uri.parse("tel: ${it.phoneNumber}")

            if (App.INSTANCE.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                telecomManager.placeCall(callUri, null)
        }


    }
}