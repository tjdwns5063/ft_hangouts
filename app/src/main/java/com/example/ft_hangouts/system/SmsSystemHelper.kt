package com.example.ft_hangouts.system

import android.app.PendingIntent
import android.telephony.SmsManager
import com.example.ft_hangouts.App

class SmsSystemHelper {
    private val smsManager: SmsManager

    init {
        try {
            smsManager = App.INSTANCE.getSystemService(SmsManager::class.java)
        } catch (err: Exception) {
            throw err
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