package com.example.ft_hangouts.system

import android.Manifest
import android.app.PendingIntent
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.R

class SmsSystemHelper(activity: AppCompatActivity) {
    private val smsManager: SmsManager = activity.getSystemService(SmsManager::class.java)
    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    private val permissionLauncher: PermissionLauncher = PermissionLauncher(
        permissions,
        activity,
        activity.getString(R.string.request_sms_permission),
        activity.getString(R.string.sms_permission_deny)
    )

    fun setCallback(callback: () -> Unit) {
        permissionLauncher.setSuccessCallback(callback)
    }

    fun registerSmsPermissionLauncher() {
        permissionLauncher.registerLauncher()
    }

    fun requestSmsPermission() {
        permissionLauncher.requestPermission()
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