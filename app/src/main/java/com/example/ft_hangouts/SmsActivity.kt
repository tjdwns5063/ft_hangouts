package com.example.ft_hangouts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivitySmsBinding

class SmsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val contact by lazy { receiveContact() }
    private var smsManager: SmsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSmsManager()
        requestPermission()

        setData()
        binding.smsSendBtn.setOnClickListener { sendMessage() }
    }

    private fun initSmsManager() {
        smsManager = applicationContext.getSystemService(SmsManager::class.java)
        smsManager ?: run {
            Toast.makeText(this, "SMS 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun requestPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS))
        }
    }

    private fun sendMessage() {
        val text = binding.sendSmsEditText.text.toString()

        smsManager?.sendTextMessage(
            contact.phoneNumber,
            null,
            text,
            null,
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
}