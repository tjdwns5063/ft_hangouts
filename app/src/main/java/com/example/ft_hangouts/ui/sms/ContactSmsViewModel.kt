package com.example.ft_hangouts.ui.sms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactSmsViewModel(
    private val contactDAO: ContactDAO,
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val smsDatabaseDAO: SmsDatabaseDAO
): ViewModel() {
    private val _messageList = MutableStateFlow<List<SmsInfo>>(emptyList())
    val messageList: StateFlow<List<SmsInfo>> = _messageList.asStateFlow()

    private val _contact = MutableStateFlow<Contact>(
        Contact(-1, "", "", "", "", "")
    )
    val contact: StateFlow<Contact> = _contact.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            getContactById(id)
            getAllMessages(contact.value.phoneNumber)
        }

    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            _contact.value = contactDAO.getItemById(id)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    private suspend fun getAllMessages(phoneNumber: String) = withContext(Dispatchers.IO) {
        try {
            _messageList.value = smsDatabaseDAO.getMessage(phoneNumber)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    fun addMessage(message: SmsInfo) {
        val lst = messageList.value.toMutableList()

        lst.add(message)
        _messageList.value = lst
    }
}

class SmsViewModelFactory(
    private val database: ContactDatabase,
    private val id: Long,
    private val smsDatabaseDAO: SmsDatabaseDAO,
    private val baseViewModel: BaseViewModel
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactSmsViewModel(
            database.contactDao(),
            id,
            baseViewModel,
            smsDatabaseDAO
        ) as T
    }
}