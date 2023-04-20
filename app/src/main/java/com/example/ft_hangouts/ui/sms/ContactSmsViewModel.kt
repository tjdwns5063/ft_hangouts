package com.example.ft_hangouts.ui.sms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.App
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactSmsViewModel(
    context: Context,
    private val id: Long,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel
    ) {
    private val smsDatabaseDAO = SmsDatabaseDAO(App.INSTANCE.contentResolver)
    private val contactDatabaseDAO = ContactDatabaseDAO(ContactHelper.createDatabase(context))

    val messageList: LiveData<List<SmsInfo>>
        get() = _messageList
    private val _messageList = MutableLiveData<List<SmsInfo>>()

    val contact: LiveData<ContactDomainModel>
        get() = _contact
    private val _contact = MutableLiveData<ContactDomainModel>()

    init {
        lifecycleScope.launch {
            initialize()
        }
    }

    private suspend fun initialize() {
        getContactById(id)
        contact.value?.let {
            getAllMessages(it.phoneNumber)
        }
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val contact = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))
            _contact.postValue(contact)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    private suspend fun getAllMessages(phoneNumber: String) = withContext(Dispatchers.IO) {
        try {
            val list = smsDatabaseDAO.getMessage(phoneNumber)
            _messageList.postValue(list)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    fun addMessage(message: SmsInfo) {
        val lst = messageList.value?.let {
            it.toMutableList()
        } ?: return

        lst.add(message)
        _messageList.value = lst
    }
}