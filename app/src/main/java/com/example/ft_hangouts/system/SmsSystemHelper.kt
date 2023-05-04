package com.example.ft_hangouts.system

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.R
import com.example.ft_hangouts.ui.detail.ContactDetailActivity

class SmsSystemHelper private constructor(private val activity: AppCompatActivity) {
    private val smsManager: SmsManager = activity.getSystemService(SmsManager::class.java)
    private lateinit var smsPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    companion object {
        private lateinit var INSTANCE: SmsSystemHelper

        fun createSmsSystemHelper(activity: AppCompatActivity): SmsSystemHelper {
            if (!this::INSTANCE.isInitialized) {
                INSTANCE = SmsSystemHelper(activity)
            }
            return INSTANCE
        }
    }

    private fun registerPermissionActivityResult() {
        if (this::smsPermissionLauncher.isInitialized)
            return

        smsPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!allPermissionGranted(it)) {
                Toast.makeText(activity.applicationContext, activity.getString(R.string.sms_permission_deny), Toast.LENGTH_SHORT).show()
                activity.finish()
            }
        }
    }

    private fun allPermissionGranted(result: Map<String, Boolean>): Boolean {
        return !result.containsValue(false)
    }

    private fun showSmsPermissionDialog(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.permission_request))
            .setMessage(activity.getString(R.string.request_sms_permission))
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton(activity.getString(R.string.cancel), null)
            .show()
    }

    fun requestPermission() {
        for (permission in permissions) {
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                showSmsPermissionDialog(smsPermissionLauncher)
                return
            }
        }
        smsPermissionLauncher.launch(permissions)
    }

    fun requestRegisterSmsPermissionLauncher() {
        registerPermissionActivityResult()
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