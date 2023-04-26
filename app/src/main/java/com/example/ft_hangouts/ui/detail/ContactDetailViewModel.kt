package com.example.ft_hangouts.ui.detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.error.*
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactDetailViewModel(
    private val lifecycleScope: CoroutineScope,
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val contactDatabaseDAO: ContactDatabaseDAO
    ) {
    private val _contact = MutableStateFlow<ContactDomainModel>(ContactDomainModel(-1, "", "", "", "", ""))
    val contact: StateFlow<ContactDomainModel> = _contact.asStateFlow()

    init {
        updateContact()
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val contact = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))

            _contact.value = contact
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    private suspend fun deleteContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            contactDatabaseDAO.deleteById(id)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseDeleteErrorHandler())
        } finally {
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        }
    }

    fun updateContact() = lifecycleScope.launch {
        getContactById(id)
    }

    fun deleteContact(id: Long) {
        lifecycleScope.launch {
            deleteContactById(id)
        }
    }
}