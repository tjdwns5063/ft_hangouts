package com.example.ft_hangouts.ui.edit

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.databinding.ActivityContactEditBinding
import com.example.ft_hangouts.ui.BaseActivity

class ContactEditActivity : BaseActivity() {
    private val binding by lazy { ActivityContactEditBinding.inflate(layoutInflater) }
    private val contact: Contact by lazy { receiveContact() }
    private val handler by lazy { if (Build.VERSION.SDK_INT >= 28) Handler.createAsync(mainLooper) else Handler(mainLooper) }
    private val viewModel by lazy { ContactEditViewModel(handler, super.baseViewModel) }

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

        viewModel.updateContactById(contact.id, newContact)
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