package com.example.ft_hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val contactDAO = ContactDatabaseDAO()
    private val id by lazy { intent.getLongExtra("id", -1) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestCallPermission()

        val contact: Contact = contactDAO.getItemById(id)?.let {
            binding.detailNameValueText.text = it.name
            binding.detailPhoneNumberValueText.text = it.phoneNumber
            binding.detailEmailValueText.text = it.email
            binding.detailGenderValueText.text = it.gender
            binding.detailRelationValueText.text = it.relation
            it
        } ?: run { Toast.makeText(this, "연락처를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            finish()
            Contact(0, "", "", "", "", "")
        }

        setBottomNavItemListener(contact)
    }

    private fun setBottomNavItemListener(contact: Contact) {
        binding.detailBottomNav.setOnItemSelectedListener { menu ->
            when(menu.itemId) {
                R.id.detail_bottom_delete -> { EventDialog.showEventDialog(
                    fragmentManager = supportFragmentManager,
                    message = "연락처를 영구히 삭제하시겠습니까?",
                    onClick = { dialog, _ -> deleteContact() }
                )
                }
                R.id.detail_bottom_sms -> { goToSmsActivity(contact) }
                R.id.detail_bottom_call -> {
//                    requestCallPermission()
                    call(contact.phoneNumber)
                }
            }
            true
        }
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
        val intent = Intent(this, SmsActivity::class.java).apply {
            putExtra("contact", contact)
        }
        startActivity(intent)
    }

    private fun deleteContact() {
        contactDAO.deleteById(id).let {
            it?.let {
                Toast.makeText(this, "연락처가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "삭제가 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
