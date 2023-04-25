package com.example.ft_hangouts.ui.search

import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactSearchViewModel(
    private val contactDatabaseDAO: ContactDatabaseDAO,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
) {
    private val _searchedList = MutableStateFlow<List<ContactDomainModel>>(emptyList())
    val searchedList: StateFlow<List<ContactDomainModel>> = _searchedList.asStateFlow()

    private suspend fun searchContact(text: String) = withContext(Dispatchers.IO) {
        try {
            _searchedList.value = contactDatabaseDAO.searchContact(text).map { contactToContactDomainModel(it) }
        } catch(err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun search(text: String) = lifecycleScope.launch {
        searchContact(text)
    }
}