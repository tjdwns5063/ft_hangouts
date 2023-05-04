package com.example.ft_hangouts.system

import android.app.PendingIntent
import android.content.Context
import android.telephony.SmsManager

class SmsSystemHelper(applicationContext: Context) {
    companion object {
        lateinit var smsManager: SmsManager

        fun createSmsManager(applicationContext: Context) {
            if (!this::smsManager.isInitialized)
                smsManager = applicationContext.getSystemService(SmsManager::class.java)
        }
    }
    init {
        createSmsManager(applicationContext)
    }

    fun sendSms(phoneNumber: String, message: String, sendIntent: PendingIntent) {
        smsManager.sendTextMessage(
            phoneNumber,
            null,
            message,
            sendIntent,
            null
        )
    }
}