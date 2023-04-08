package com.example.ft_hangouts

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import com.example.ft_hangouts.sms.SmsInfo

/*
    왜 recyclerview.scrollToPosition(idx)가 handler.postDelayed로 딜레이를 줘야만 제대로 동작할까...?

    추측 1. DiffUtil의 비교과정이 비동기로 이루어지는데 그게 끝나기 전에 scrollToPosition이 호출돼서 씹힘.
    (맞는지 아닌지 DiffUtil 제거하고 체크해보자..)
    진심 맞는듯? 제거하니까 딜레이 안줘도 됨;;;
 */

class ContactSmsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val contact by lazy { receiveContact() }
    private var smsManager: SmsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSmsManager()
        registerSmsReceiver()
        setData()
        requestPermission()
        setRecyclerView()
        binding.smsSendBtn.setOnClickListener { sendMessage() }
    }

    private fun setRecyclerView() {
        val list = BackgroundHelper.execute { readSms() }
        val adapter = SmsChatRecyclerAdapter(list.toMutableList())

        list ?: run { Toast.makeText(this, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show() }
        binding.smsChatRecyclerView.adapter = adapter
        binding.smsChatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.smsChatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun registerSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    val message = binding.sendSmsEditText.text.toString()
                    val adapter = binding.smsChatRecyclerView.adapter as SmsChatRecyclerAdapter
                    val curr = adapter.currentList.toMutableList()

                    curr.add(SmsInfo(message, 2))
                    adapter.update(curr)
                    binding.sendSmsEditText.text.clear()
                    binding.smsChatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                } else {
                    EventDialog("문자 메세지 전송이 실패했습니다. 재전송 하시겠습니까?") { dialog, _ ->
                        sendMessage()
                    }
                }
            }
        }, IntentFilter("SEND_SMS_SUCCESS"))

        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val newMessage = parseSmsMessage(it)
                    val adapter = binding.smsChatRecyclerView.adapter as SmsChatRecyclerAdapter
                    val curr = adapter.currentList.toMutableList()

                    curr.add(SmsInfo(newMessage, 1))
                    adapter.update(curr)
                    binding.smsChatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }
        }, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    private fun initSmsManager() {
        smsManager = getSystemService(SmsManager::class.java)
        smsManager ?: run {
            Toast.makeText(this, "SMS 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun requestPermission() {
        val permissions = arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val allPermissionGranted = it[permissions[0]] == true && it[permissions[1]] == true && it[permissions[2]] == true
            if (!allPermissionGranted) {
                finish()
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun sendMessage() {
        val text = binding.sendSmsEditText.text.toString()
        val intent = Intent("SEND_SMS_SUCCESS")
        val sendIntent = PendingIntent.getBroadcast(this, 11, intent, PendingIntent.FLAG_IMMUTABLE)

        smsManager?.sendTextMessage(
            contact.phoneNumber,
            null,
            text,
            sendIntent,
            null
        )
    }

    private fun setData() {
        binding.smsProfileName.text = contact.name
    }

    private fun receiveContact(): Contact {
        return if (Build.VERSION.SDK_INT < 33) {
            intent.getSerializableExtra("contact") as Contact
        } else {
            intent.getSerializableExtra("contact", Contact::class.java) as Contact
        }
    }

    private fun readSms(): List<SmsInfo> {
        val numberCol = Telephony.TextBasedSmsColumns.ADDRESS
        val textCol = Telephony.TextBasedSmsColumns.BODY
        val typeCol = Telephony.TextBasedSmsColumns.TYPE

        val projection = arrayOf(numberCol, textCol, typeCol)
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            "$numberCol=?",
            arrayOf(contact.phoneNumber),
            null
        )

        val textColIdx = cursor!!.getColumnIndex(textCol)
        val typeColIdx = cursor.getColumnIndex(typeCol)

        val smsList = mutableListOf<SmsInfo>()
        while (cursor.moveToNext()) {
            val text = cursor.getString(textColIdx)
            val type = cursor.getInt(typeColIdx)

            smsList += SmsInfo(text, type)
        }
        cursor.close()
        return smsList.reversed()
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