package com.example.ft_hangouts.system

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import com.example.ft_hangouts.App

object CallSystemHelper {
    private val telecomManager = App.INSTANCE.getSystemService(TelecomManager::class.java)

    private fun parseUri(address: String): Uri {
        return Uri.parse("tel: $address")
    }

    private fun call(address: Uri) {
        if (App.INSTANCE.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            telecomManager.placeCall(address, null)
        }
    }

    fun callToAddress(address: String) {
        call(parseUri(address))
    }
}