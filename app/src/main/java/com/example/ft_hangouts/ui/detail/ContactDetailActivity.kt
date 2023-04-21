package com.example.ft_hangouts.ui.detail

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding
import com.example.ft_hangouts.system.CallSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import com.example.ft_hangouts.ui.edit.ContactEditActivity
import com.example.ft_hangouts.ui.sms.ContactSmsActivity


class ContactDetailActivity : BaseActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val id by lazy { intent.getLongExtra("id", -1) }
    private val viewModel by lazy { ContactDetailViewModel(applicationContext, lifecycleScope, id, super.baseViewModel) }
    private lateinit var callPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView(binding.root)
        callPermissionLauncher = registerRequestCallPermissionResult()
        roundProfileBorder()
        setBottomNavItemListener()
        setContactObservationForProfileUpdates()
    }

    private fun roundProfileBorder() {
        binding.detailProfileImage.clipToOutline = true
    }

    private fun setContactObservationForProfileUpdates() {
        viewModel.contact.observe(this) {
            it.profile?.let { profile -> binding.detailProfileImage.setImageBitmap(profile) }
        }
    }

    private fun registerRequestCallPermissionResult(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestCallToCallSystemHelper()
            } else {
                Toast.makeText(this, getString(R.string.detail_permission_deny_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateContact()
    }

    private fun setBottomNavItemListener() {
        binding.detailBottomNav.setOnItemSelectedListener { menu ->
            when(menu.itemId) {
                R.id.detail_bottom_delete -> {
                    EventDialog.showEventDialog(
                        fragmentManager = supportFragmentManager,
                        message = getString(R.string.check_delete_message),
                        onClick = { _, _ -> viewModel.deleteContact(id) })
                }
                R.id.detail_bottom_sms -> { goToSmsActivity() }
                R.id.detail_bottom_call -> { requestCallPermission() }
                R.id.detail_bottom_edit -> { goToContactEditActivity() }
            }
            true
        }
    }

    private fun goToContactEditActivity() {
        val intent = Intent(this, ContactEditActivity::class.java).apply {
            putExtra(CONTACT_ID, id)
        }

        startActivity(intent)
    }

    private fun requestCallPermission() {
        when {
            checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED -> {
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                showCallPermissionDialog()
            }
            else -> {
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun showCallPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_request))
            .setMessage(getString(R.string.call_permission_request))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun requestCallToCallSystemHelper() {
        try {
            CallSystemHelper().callToAddress(viewModel.contact.value!!.phoneNumber)
        } catch (err: Exception) {
            Toast.makeText(this, getString(R.string.cannot_use_call_feature), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToSmsActivity() {
        val intent = Intent(this, ContactSmsActivity::class.java).apply {
            putExtra(CONTACT_ID, id)
        }
        startActivity(intent)
    }
}

/*
    배운점: 권한 설정은 반드시 OnCreate에서 해야함.... (정확히는 액티비티가 STARTED 상태 전에!)
 */
