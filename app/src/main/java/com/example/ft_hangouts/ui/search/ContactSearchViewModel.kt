package com.example.ft_hangouts.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactSearchViewModel(
    private val contactDAO: ContactDAO,
    private val baseViewModel: BaseViewModel
): ViewModel() {
    private val _searchedList = MutableStateFlow<List<Contact>>(emptyList())
    val searchedList: StateFlow<List<Contact>> = _searchedList.asStateFlow()

    private val _text = MutableStateFlow<String>("")

    private fun searchContact(text: String) {
        try {
            _searchedList.value = contactDAO.search(text).map { Contact.from(it) }
            _text.value = text
        } catch(err: Exception) {
            _searchedList.value = emptyList()
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun update(text: String?): Boolean {
        if (text.isNullOrEmpty()) {
            clearList()
            return false
        }
        viewModelScope.launch {
            search(_text.value)
        }
        return true
    }

    suspend fun search(text: String) = withContext(Dispatchers.IO) {
        searchContact("$text%")
    }

    private fun clearList() {
        _searchedList.value = listOf()
    }
}

class SearchViewModelFactory(
    private val database: ContactDatabase,
    private val baseViewModel: BaseViewModel
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactSearchViewModel(
            database.contactDao(),
            baseViewModel
        ) as T
    }
}