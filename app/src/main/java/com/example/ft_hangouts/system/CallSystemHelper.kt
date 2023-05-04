package com.example.ft_hangouts.system

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R

class CallSystemHelper private constructor(private val activity: AppCompatActivity) {
    private lateinit var callPermissionLauncher: ActivityResultLauncher<String>
    private val telecomManager: TelecomManager = activity.getSystemService(TelecomManager::class.java)

    companion object {
        private lateinit var INSTANCE: CallSystemHelper

        fun createCallSystemHelper(activity: AppCompatActivity): CallSystemHelper {
            if (!this::INSTANCE.isInitialized)
                INSTANCE = CallSystemHelper(activity)
            return INSTANCE
        }
    }

    private fun parseUri(address: String): Uri {
        return Uri.parse("tel: $address")
    }

    private fun call(address: Uri) {
        if (App.INSTANCE.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            telecomManager.placeCall(address, null)
        }
    }

    private fun registerRequestCallPermissionResult() {
        if (this::callPermissionLauncher.isInitialized)
            return

        callPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    activity.applicationContext,
                    activity.getString(R.string.detail_permission_deny_message),
                    Toast.LENGTH_SHORT
                ).show()
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

    fun requestCallPermission() {
        when {
            activity.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED -> {
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE) -> {
                showCallPermissionDialog(activity, callPermissionLauncher)
            }
            else -> {
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    fun requestRegisterCallPermissionLauncher() {
        registerRequestCallPermissionResult()
    }

    fun callToAddress(address: String) {
        call(parseUri(address))
    }
}