package com.example.ft_hangouts.ui.sms

import android.app.PendingIntent
import android.os.Handler
import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.App
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.error.DatabaseErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel

class ContactSmsViewModel(private val handler: Handler, private val baseViewModel: BaseViewModel, contact: Contact) {
    private val smsDatabaseDAO = SmsDatabaseDAO(App.INSTANCE.contentResolver)

    val messageList: LiveData<List<SmsInfo>>
        get() = _messageList
    private val _messageList = MutableLiveData<List<SmsInfo>>()

    val errorHandler: LiveData<DatabaseErrorHandler>
        get() = _errorHandler
    private val _errorHandler = MutableLiveData<DatabaseErrorHandler>()

    val contact: LiveData<Contact>
        get() = _contact
    private val _contact = MutableLiveData<Contact>()

    init {
        getAllMessages()
        _contact.value = contact
    }

    private fun getAllMessages() {
        BackgroundHelper.execute {
            try {
                val phoneNumber = contact.value?.let { it.phoneNumber } ?: return@execute
                val list = smsDatabaseDAO.getMessage(phoneNumber)
                handler.post { _messageList.value = list }
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseReadErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(null) }
            }
        }
    }

    fun sendMessage(smsManager: SmsManager, message: String, sendIntent: PendingIntent) {
        val phoneNumber = contact.value?.let { it.phoneNumber } ?: return

        smsManager.sendTextMessage(
            phoneNumber,
            null,
            message,
            sendIntent,
            null
        )
    }

    fun addMessage(message: SmsInfo) {
        val lst = messageList.value?.let {
            it.toMutableList()
        } ?: return

        lst.add(message)
        _messageList.value = lst
    }
}