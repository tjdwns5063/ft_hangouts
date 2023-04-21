package com.example.ft_hangouts.ui.sms

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.system.SmsSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID

/*
    왜 recyclerview.scrollToPosition(idx)가 handler.postDelayed로 딜레이를 줘야만 제대로 동작할까...?

    추측 1. DiffUtil의 비교과정이 비동기로 이루어지는데 그게 끝나기 전에 scrollToPosition이 호출돼서 씹힘.
    (맞는지 아닌지 DiffUtil 제거하고 체크해보자..)
    진심 맞는듯? 제거하니까 딜레이 안줘도 됨;;;
 */

/*
    권한 설정만 해주는 클래스 만들면 좋을듯
    브로드캐스트 리시버 따로 빼자...
 */

class ContactSmsActivity : BaseActivity() {
    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    private val id by lazy { intent.getLongExtra(CONTACT_ID, -1) }
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val smsSystemHelper = createSmsSystemHelper()
    private val viewModel by lazy { ContactSmsViewModel(applicationContext, id, lifecycleScope, super.baseViewModel) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        smsSystemHelper ?: run { finish() }
        val launcher = registerPermissionActivityResult()
        requestPermission(launcher)
    }

    private fun createSmsSystemHelper(): SmsSystemHelper? {
        return try {
            SmsSystemHelper()
        } catch (err: Exception) {
            Toast.makeText(this, getString(R.string.cannot_use_sms_feature), Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun registerPermissionActivityResult(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val allPermissionGranted = it[permissions[0]] == true && it[permissions[1]] == true && it[permissions[2]] == true
            if (allPermissionGranted) {
                registerSmsReceiver()
                setRecyclerView()
                binding.smsSendBtn.setOnClickListener { onClickSmsSendButton() }
            } else {
                Toast.makeText(this, getString(R.string.sms_permission_deny), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showSmsPermissionDialog(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_request))
            .setMessage(getString(R.string.request_sms_permission))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun requestPermission(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                showSmsPermissionDialog(permissionLauncher)
                return
            }
        }
        permissionLauncher.launch(permissions)
    }

    private fun registerSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    val message = binding.sendSmsEditText.text.toString()

                    viewModel.addMessage(SmsInfo(message, System.currentTimeMillis(), 2))
                    binding.sendSmsEditText.text.clear()
                } else {
                    EventDialog(getString(R.string.message_of_sms_send_failure)) { dialog, _ ->
                        onClickSmsSendButton()
                    }
                }
            }
        }, IntentFilter("SEND_SMS_SUCCESS"))

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

        viewModel.messageList.observe(this) {
            it?.let {
                adapter.submitList(it)
            }
        }

        binding.smsChatRecyclerView.adapter = adapter
        binding.smsChatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.smsChatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun onClickSmsSendButton() {
        smsSystemHelper?.sendSms(
            phoneNumber = viewModel.contact.value!!.phoneNumber,
            message = binding.sendSmsEditText.text.toString(),
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