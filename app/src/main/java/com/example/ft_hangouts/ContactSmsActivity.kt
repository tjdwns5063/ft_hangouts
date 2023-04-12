package com.example.ft_hangouts

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import com.example.ft_hangouts.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.sms_database.SmsInfo

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

class ContactSmsActivity : AppCompatActivity() {
    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    private val handler by lazy {if (Build.VERSION.SDK_INT >= 28) Handler.createAsync(mainLooper) else Handler(mainLooper)}
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val contact by lazy { receiveContact() }
    private var smsManager: SmsManager? = null
    private val smsDatabaseDAO by lazy { SmsDatabaseDAO(contentResolver) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val launcher = registerPermissionActivityResult()
        requestPermission(launcher)
    }

    private fun registerPermissionActivityResult(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val allPermissionGranted = it[permissions[0]] == true && it[permissions[1]] == true && it[permissions[2]] == true
            if (allPermissionGranted) {
                initSmsManager()
                registerSmsReceiver()
                setData()
                setRecyclerView()
                binding.smsSendBtn.setOnClickListener { sendMessage() }
            } else {
                finish()
            }
        }
    }

    private fun requestPermission(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher.launch(permissions)
    }

    private fun initSmsManager() {
        smsManager = getSystemService(SmsManager::class.java)
        smsManager ?: run {
            Toast.makeText(this, "SMS 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun registerSmsReceiver() {
        registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == Activity.RESULT_OK) {
                    val message = binding.sendSmsEditText.text.toString()
                    val adapter = binding.smsChatRecyclerView.adapter as SmsChatRecyclerAdapter
                    val curr = adapter.currentList.toMutableList()

                    curr.add(SmsInfo(message, System.currentTimeMillis(),2))
                    adapter.submitList(curr)
                    binding.sendSmsEditText.text.clear()
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

                    curr.add(SmsInfo(newMessage, System.currentTimeMillis(), 1))
                    adapter.submitList(curr)
                }
            }
        }, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    private fun setData() {
        binding.smsProfileName.text = contact.name
    }
    private fun setRecyclerView() {
        val adapter = SmsChatRecyclerAdapter { len -> binding.smsChatRecyclerView.scrollToPosition(len) }

        binding.smsChatRecyclerView.adapter = adapter
        binding.smsChatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.smsChatRecyclerView.scrollToPosition(adapter.itemCount - 1)

        BackgroundHelper.execute {
            try {
                val list = smsDatabaseDAO.getMessage(contact.phoneNumber)
                handler.post { adapter.submitList(list) }
            } catch (err: Exception) {
                handler.post {
                    Toast.makeText(this, "문자메세지를 불러온는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
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

    private fun receiveContact(): Contact {
        return if (Build.VERSION.SDK_INT < 33) {
            intent.getSerializableExtra("contact") as Contact
        } else {
            intent.getSerializableExtra("contact", Contact::class.java) as Contact
        }
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