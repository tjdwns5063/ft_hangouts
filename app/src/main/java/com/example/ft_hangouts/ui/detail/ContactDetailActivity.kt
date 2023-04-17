package com.example.ft_hangouts.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding
import com.example.ft_hangouts.ui.BaseActivity
import com.example.ft_hangouts.ui.edit.ContactEditActivity
import com.example.ft_hangouts.ui.sms.ContactSmsActivity

class ContactDetailActivity : BaseActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val id by lazy { intent.getLongExtra("id", -1) }
    private val handler by lazy { if (Build.VERSION.SDK_INT >= 28) Handler.createAsync(mainLooper) else Handler(mainLooper) }
    private val viewModel by lazy { ContactDetailViewModel(handler, id) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        setContentView(binding.root)
        requestCallPermission()
        viewModel.contact.observe(this) {
            it?.let { setBottomNavItemListener(it) }
        }
    }


    private fun setBottomNavItemListener(contact: Contact) {
        binding.detailBottomNav.setOnItemSelectedListener { menu ->
            when(menu.itemId) {
                R.id.detail_bottom_delete -> {
                    EventDialog.showEventDialog(
                        fragmentManager = supportFragmentManager,
                        message = "연락처를 영구히 삭제하시겠습니까?",
                        onClick = { _, _ -> viewModel.deleteContactById(id) })
                }
                R.id.detail_bottom_sms -> { goToSmsActivity(contact) }
                R.id.detail_bottom_call -> { call(contact.phoneNumber) }
                R.id.detail_bottom_edit -> { goToContactEditActivity(contact) }
            }
            true
        }
    }

    private fun goToContactEditActivity(contact: Contact) {
        val intent = Intent(this, ContactEditActivity::class.java).apply {
            putExtra("contact", contact)
        }

        startActivity(intent)
    }

    private fun requestCallPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "권한을 설정을 거절하셨습니다. 몇가지 기능이 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
    }

    private fun call(phoneNumber: String) {
        val telecomManager = getSystemService(TelecomManager::class.java)

        if (telecomManager == null) {
            Toast.makeText(this, "전화기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                telecomManager.placeCall(Uri.parse("tel: $phoneNumber"), null)
            }
            else {
                Toast.makeText(this, "전화에 필요한 권한이 없습니다. 권한을 설정하고 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToSmsActivity(contact: Contact) {
        val intent = Intent(this, ContactSmsActivity::class.java).apply {
            putExtra("contact", contact)
        }
        startActivity(intent)
    }
}

/*
    배운점: 권한 설정은 반드시 OnCreate에서 해야함.... (정확히는 액티비티가 STARTED 상태 전에!)
 */
