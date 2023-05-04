package com.example.ft_hangouts.system

import android.app.PendingIntent
import android.content.Context
import android.telephony.SmsManager

class SmsSystemHelper private constructor(applicationContext: Context) {
    val smsManager: SmsManager = applicationContext.getSystemService(SmsManager::class.java)
    companion object {
        private lateinit var INSTANCE: SmsSystemHelper

        fun createSmsSystemHelper(applicationContext: Context): SmsSystemHelper {
            if (!this::INSTANCE.isInitialized)
                INSTANCE = SmsSystemHelper(applicationContext)
            return INSTANCE
        }
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