package com.example.ft_hangouts

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.ft_hangouts.contact_database.Contact
import com.example.ft_hangouts.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactEditBinding

class ContactEditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityContactEditBinding.inflate(layoutInflater) }
    private val contact: Contact by lazy { receiveContact() }
    private val databaseDAO = ContactDatabaseDAO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        receiveContact()
        setData()
        binding.editOkButton.setOnClickListener { contactEditLogic() }
        binding.editCancelButton.setOnClickListener { finish() }
    }

    private fun checkEditText(): Boolean {
        return binding.editNameEditText.text.isNotBlank() && binding.editPhoneNumberEditText.text.isNotBlank()
    }

    private fun contactEditLogic() {
        if (!checkEditText()) {
            Toast.makeText(this, "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val newContact = Contact(
            id = 0,
            name = binding.editNameEditText.text.toString(),
            email = binding.editEmailEditText.text.toString(),
            phoneNumber = binding.editPhoneNumberEditText.text.toString(),
            gender = binding.editGenderEditText.text.toString(),
            relation = binding.editRelationEditText.text.toString()
        )
        val result = databaseDAO.updateById(contact.id, newContact)

        if (result != 0) {
            Toast.makeText(this, "연락처가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "연락처 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun setData() {
        binding.editNameEditText.setText(contact.name)
        binding.editPhoneNumberEditText.setText(contact.phoneNumber)
        binding.editEmailEditText.setText(contact.email)
        binding.editGenderEditText.setText(contact.gender)
        binding.editRelationEditText.setText(contact.relation)
    }

    private fun receiveContact(): Contact {
        return if (Build.VERSION.SDK_INT >= 33)
            intent.getSerializableExtra("contact", Contact::class.java) as Contact
        else
            intent.getSerializableExtra("contact") as Contact
    }
}