package com.example.ft_hangouts.ui.search

import com.example.ft_hangouts.data.contact_database.ContactDAO
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
    private val contactDAO: ContactDAO,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
) {
    private val _searchedList = MutableStateFlow<List<ContactDomainModel>>(emptyList())
    val searchedList: StateFlow<List<ContactDomainModel>> = _searchedList.asStateFlow()

    private val _text = MutableStateFlow<String>("")

    private suspend fun searchContact(text: String) = withContext(Dispatchers.IO) {
        try {
            _searchedList.value = contactDAO.search(text).map { contactToContactDomainModel(it) }
            _text.value = text
        } catch(err: Exception) {
            _searchedList.value = emptyList()
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun update() {
        if (_text.value != "") {
            search(_text.value)
        }
    }

    fun search(text: String) = lifecycleScope.launch {
        searchContact("$text%")
    }
}