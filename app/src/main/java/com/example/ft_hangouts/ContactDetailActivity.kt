package com.example.ft_hangouts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.database.ContactDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val contactDAO = ContactDatabaseDAO()
    private val id by lazy { intent.getLongExtra("id", -1) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        binding.detailBottomNav.setOnItemSelectedListener { menu ->
            when(menu.itemId) {
                R.id.detail_bottom_delete -> { EventDialog.showEventDialog(
                    fragmentManager = supportFragmentManager,
                    message = "연락처를 영구히 삭제하시겠습니까?",
                    onClick = { dialog, _ -> deleteContact() }
                    )
                }

                R.id.detail_bottom_sms -> { goToSmsActivity(contact) }
            }
            true
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