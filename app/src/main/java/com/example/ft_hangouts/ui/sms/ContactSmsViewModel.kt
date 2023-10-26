package com.example.ft_hangouts.ui.sms

import android.app.PendingIntent
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.system.SmsSystemHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactSmsViewModel(
    private val contactDAO: ContactDAO,
    private val id: Long,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel,
    private val smsDatabaseDAO: SmsDatabaseDAO
) {
    private val _messageList = MutableStateFlow<List<SmsInfo>>(emptyList())
    val messageList: StateFlow<List<SmsInfo>> = _messageList.asStateFlow()

    private val _contact = MutableStateFlow<ContactDomainModel>(
        ContactDomainModel(-1, "", "", "", "", "")
    )
    val contact: StateFlow<ContactDomainModel> = _contact.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() = lifecycleScope.launch {
        getContactById(id)
        getAllMessages(contact.value.phoneNumber)
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            _contact.value = contactToContactDomainModel(contactDAO.getItemById(id))
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