package com.example.ft_hangouts.system

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R

class CallSystemHelper(activity: AppCompatActivity) {
    private val telecomManager: TelecomManager = activity.getSystemService(TelecomManager::class.java)
    private val permissions: Array<String> = arrayOf(Manifest.permission.CALL_PHONE)
    private val permissionLauncher: PermissionLauncher = PermissionLauncher(
        permissions,
        activity,
        activity.getString(R.string.call_permission_request),
        activity.getString(R.string.call_permission_deny)
    )

    private fun parseUri(phoneNumber: String): Uri {
        return Uri.parse("tel: $phoneNumber")
    }

    fun registerCallPermissionLauncher() {
        permissionLauncher.registerLauncher()
    }

    fun requestCallPermission() {
        permissionLauncher.requestPermission()
    }

    fun call(phoneNumber: String) {
        if (App.INSTANCE.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            telecomManager.placeCall(parseUri(phoneNumber), null)
        }
    }
}