package com.example.ft_hangouts.ui.detail

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding
import com.example.ft_hangouts.system.registerRequestCallPermissionResult
import com.example.ft_hangouts.system.requestCallPermission
import com.example.ft_hangouts.system.requestCallToCallSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import com.example.ft_hangouts.ui.edit.ContactEditActivity
import com.example.ft_hangouts.ui.sms.ContactSmsActivity


class ContactDetailActivity : BaseActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val id by lazy { intent.getLongExtra(CONTACT_ID, -1) }
    private val viewModel by lazy { ContactDetailViewModel(applicationContext, lifecycleScope, id, super.baseViewModel) }
    private val callPermissionLauncher = registerRequestCallPermissionResult()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView(binding.root)
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
                R.id.detail_bottom_sms -> { goToActivity(ContactSmsActivity::class.java, CONTACT_ID, id) }
                R.id.detail_bottom_call -> {
                    requestCallPermission(callPermissionLauncher)
                    viewModel.contact.value?.let {
                        val phoneNumber = it.phoneNumber
                        requestCallToCallSystemHelper(phoneNumber)
                    }
                }
                R.id.detail_bottom_edit -> { goToActivity(ContactEditActivity::class.java, CONTACT_ID, id) }
            }
            true
        }
    }
}

/*
    배운점: 권한 설정은 반드시 OnCreate에서 해야함.... (정확히는 액티비티가 STARTED 상태 전에!)
 */
