package com.example.ft_hangouts.system

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R
import com.example.ft_hangouts.ui.base.BaseActivity

fun BaseActivity.requestCallToCallSystemHelper(phoneNumber: String) {
    try {
        CallSystemHelper(applicationContext).callToAddress(phoneNumber)
    } catch (err: Exception) {
        Toast.makeText(this, getString(R.string.cannot_use_call_feature), Toast.LENGTH_SHORT).show()
    }
}

fun BaseActivity.registerRequestCallPermissionResult(): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, getString(R.string.detail_permission_deny_message), Toast.LENGTH_SHORT).show()
        }
    }
}

private fun showCallPermissionDialog(context: Context, callPermissionLauncher: ActivityResultLauncher<String>) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.permission_request))
        .setMessage(context.getString(R.string.call_permission_request))
        .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
        .setNegativeButton(context.getString(R.string.cancel), null)
        .show()
}

fun BaseActivity.requestCallPermission(
    callPermissionLauncher: ActivityResultLauncher<String>,
) {
    when {
        checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED -> {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
        shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
            showCallPermissionDialog(this, callPermissionLauncher)
        }
        else -> {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }
}

class CallSystemHelper(applicationContext: Context) {
    companion object {
        lateinit var telecomManager: TelecomManager

        fun createSmsManager(applicationContext: Context) {
            if (!this::telecomManager.isInitialized)
                telecomManager = applicationContext.getSystemService(TelecomManager::class.java)
        }
    }
    init {
        createSmsManager(applicationContext)
    }

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