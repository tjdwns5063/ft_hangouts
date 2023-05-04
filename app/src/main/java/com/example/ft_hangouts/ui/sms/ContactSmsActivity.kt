package com.example.ft_hangouts.ui.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.system.SmsSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import kotlinx.coroutines.launch

class ContactSmsActivity : BaseActivity() {
    private val id by lazy { intent.getLongExtra(CONTACT_ID, -1) }
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private lateinit var viewModel: ContactSmsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel()
        if (this::viewModel.isInitialized)
            viewModel.requestPermission()
        setContentView(binding.root)
        registerSmsReceiver()
        setRecyclerView()
        binding.smsSendBtn.setOnClickListener { onClickSmsSendButton() }
    }

    private fun createViewModel() {
        try {
            viewModel = ContactSmsViewModel(
                ContactDatabaseDAO(ContactHelper.createDatabase(applicationContext)),
                id,
                lifecycleScope,
                super.baseViewModel,
                SmsDatabaseDAO(contentResolver),
                SmsSystemHelper.createSmsSystemHelper(this)
            )
        } catch (err: Exception) {
            // baseViewModel.submitHandler(SystemError)
            finish()
        }
    }

//    private fun createSmsSystemHelper(): SmsSystemHelper? {
//        return try {
//            SmsSystemHelper.createSmsSystemHelper(applicationContext)
//        } catch (err: Exception) {
//            Toast.makeText(this, getString(R.string.cannot_use_sms_feature), Toast.LENGTH_SHORT).show()
//            null
//        }
//    }

    private fun registerSmsReceiver() {
        registerSendSmsReceiver()
        registerReceiveSmsReceiver()
    }

    private fun registerSendSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    val message = binding.sendSmsEditText.text.toString()

                    viewModel.addMessage(SmsInfo(message, System.currentTimeMillis(), 2))
                    binding.sendSmsEditText.text.clear()
                } else {
                    EventDialog(getString(R.string.message_of_sms_send_failure)) { _, _ ->
                        onClickSmsSendButton()
                    }
                }
            }
        }, IntentFilter("SEND_SMS_SUCCESS"))
    }

    private fun registerReceiveSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val newMessage = parseSmsMessage(it)
                    viewModel.addMessage(SmsInfo(newMessage, System.currentTimeMillis(), 1))
                }
            }
        }, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    private fun setRecyclerView() {
        val adapter = SmsChatRecyclerAdapter { len -> binding.smsChatRecyclerView.scrollToPosition(len) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messageList.collect {
                    adapter.submitList(it)
                }
            }
        }
        binding.smsChatRecyclerView.adapter = adapter
        binding.smsChatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.smsChatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun onClickSmsSendButton() {
        viewModel.sendSms(
            text = binding.sendSmsEditText.text.toString(),
            sendIntent = PendingIntent.getBroadcast(
                this,
                11,
                Intent("SEND_SMS_SUCCESS"),
                PendingIntent.FLAG_IMMUTABLE)
        )
    }

    private fun parseSmsMessage(intent: Intent): String {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val receiveMessage = StringBuilder()

        for (smsMessage in messages) {
            receiveMessage.append(smsMessage.messageBody)
        }
        return receiveMessage.toString()
    }
}