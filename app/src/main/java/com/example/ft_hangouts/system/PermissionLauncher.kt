package com.example.ft_hangouts.system

import android.app.AlertDialog
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.R

class PermissionLauncher(
    private val permissions: Array<String>,
    private val activity: AppCompatActivity,
    private val requestMessage: String,
    private val denyMessage: String
) {
    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    private var successCallback: () -> Unit = {}

    private fun allPermissionGranted(result: Map<String, Boolean>): Boolean {
        return !result.containsValue(false)
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.permission_request))
            .setMessage(requestMessage)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                launcher.launch(permissions)
            }
            .setNegativeButton(activity.getString(R.string.cancel), null)
            .show()
    }

    fun setSuccessCallback(callback: () -> Unit) {
        successCallback = callback
    }

    fun registerLauncher() {
        launcher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!allPermissionGranted(it)) {
                Toast.makeText(activity.applicationContext, denyMessage, Toast.LENGTH_SHORT).show()
            } else {
                successCallback()
            }
        }
    }

    fun requestPermission() {
        for (permission in permissions) {
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                showPermissionDialog()
                return
            }
        }
        launcher.launch(permissions)
    }
}