package com.example.ft_hangouts

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.databinding.ActivitySmsBinding
import java.security.Permission
import java.util.jar.Manifest

class SmsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val contact by lazy { receiveContact() }
    private lateinit var smsManager: SmsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        smsManager = applicationContext.getSystemService(SmsManager::class.java)
        requestPermission()

        setData()
        binding.smsSendBtn.setOnClickListener { sendMessage() }
    }

    private fun requestPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.i("ret", "$it")
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_SMS))
        }
    }

    private fun sendMessage() {
        val text = binding.sendSmsEditText.text.toString()

        smsManager.sendTextMessage(
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