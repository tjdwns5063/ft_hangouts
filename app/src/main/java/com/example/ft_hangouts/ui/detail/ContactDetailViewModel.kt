package com.example.ft_hangouts.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.error.*
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactDetailViewModel(
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val contactDAO: ContactDAO
    ): ViewModel() {
    private val _contact = MutableStateFlow<Contact>(Contact(-1, "", "", "", "", ""))
    val contact: StateFlow<Contact> = _contact.asStateFlow()

    init {
        initContact()
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val contact = Contact.from(contactDAO.getItemById(id))
            _contact.value = contact
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    private suspend fun deleteContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            contactDAO.deleteById(id)
            baseViewModel.submitHandler(DatabaseSuccessHandler().apply { this.updateTerminated(true) })
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseDeleteErrorHandler())
        }
    }

    fun initContact() {
        viewModelScope.launch {
            getContactById(id)
        }
    }

    suspend fun deleteContact()  {
        deleteContactById(id)
    }
}

class DetailViewModelFactory(
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val database: ContactDatabase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactDetailViewModel(
            id,
            baseViewModel,
            database.contactDao()
        ) as T
    }
}