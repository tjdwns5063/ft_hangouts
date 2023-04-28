package com.example.ft_hangouts.ui.detail

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding
import com.example.ft_hangouts.system.registerRequestCallPermissionResult
import com.example.ft_hangouts.system.requestCallPermission
import com.example.ft_hangouts.system.requestCallToCallSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import com.example.ft_hangouts.ui.edit.ContactEditActivity
import com.example.ft_hangouts.ui.sms.ContactSmsActivity
import kotlinx.coroutines.launch


class ContactDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityContactDetailBinding
    private val id by lazy { intent.getLongExtra(CONTACT_ID, -1) }
    private val viewModel by lazy {
        ContactDetailViewModel(lifecycleScope, id, baseViewModel, ContactDatabaseDAO(ContactHelper.createDatabase(this)))
    }
    private val callPermissionLauncher = registerRequestCallPermissionResult()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        roundProfileBorder()
        setBottomNavItemListener()
        setContactObservationForProfileUpdates()
        setContentView(binding.root)

    }

    private fun roundProfileBorder() {
        binding.detailProfileImage.clipToOutline = true
    }

    private fun setContactObservationForProfileUpdates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateContact()
                viewModel.contact.collect {
                    it.profile?.let { profile -> binding.detailProfileImage.setImageBitmap(profile) }
                }
            }
        }
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
                    requestCallToCallSystemHelper(viewModel.contact.value.phoneNumber)
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
