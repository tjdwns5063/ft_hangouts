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
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import com.example.ft_hangouts.sms.SmsInfo

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
        val adapter = SmsChatRecyclerAdapter()
        binding.smsChatRecyclerView.adapter = adapter
        binding.smsChatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.submitList(readSms().reversed())
    }

    private fun registerSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    val list = readSms().reversed()
                    (binding.smsChatRecyclerView.adapter as SmsChatRecyclerAdapter).submitList(list)
                }
            }
        }, IntentFilter("SEND_SMS_SUCCESS"))

        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val newMessage = parseSmsMessage(it)
                    val list = readSms().reversed().toMutableList()
                    list += SmsInfo(newMessage, 1)
                    (binding.smsChatRecyclerView.adapter as SmsChatRecyclerAdapter).submitList(list)
                }
            }
        }, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    private fun initSmsManager() {
        smsManager = applicationContext.getSystemService(SmsManager::class.java)
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
        val sendIntent = PendingIntent.getBroadcast(this, 11, Intent("SEND_SMS_SUCCESS"), PendingIntent.FLAG_IMMUTABLE)

        smsManager?.sendTextMessage(
            contact.phoneNumber,
            null,
            text,
            sendIntent,
            null
        )
        binding.sendSmsEditText.text.clear()
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
        return smsList
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