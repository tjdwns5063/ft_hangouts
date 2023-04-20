package com.example.ft_hangouts.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactEditBinding
import com.example.ft_hangouts.ui.BaseActivity
import com.example.ft_hangouts.ui.ContactActivityContract.CONTACT_ID

class ContactEditActivity : BaseActivity() {
    private val id by lazy { receiveId() }
    private val binding by lazy { ActivityContactEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ContactEditViewModel(id, lifecycleScope, super.baseViewModel, ImageDatabaseDAO(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageSelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data ?: return@registerForActivityResult

                viewModel.updateProfileImage(uri)
            }
        }

        binding.editProfileImage.clipToOutline = true

        viewModel.contact.observe(this) {
            if (it.profile != null) {
                binding.editProfileImage.setImageBitmap(it.profile)
            }
        }

        viewModel.updatedProfile.observe(this) {
            binding.editProfileImage.setImageDrawable(it)
        }

        binding.editProfileImage.setOnClickListener {
            imageSelectLauncher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
        }

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

        viewModel.updateContact(
            name = binding.editNameEditText.text.toString(),
            email = binding.editEmailEditText.text.toString(),
            phoneNumber = binding.editPhoneNumberEditText.text.toString(),
            gender = binding.editGenderEditText.text.toString(),
            relation = binding.editRelationEditText.text.toString()
        )
        finish()
    }

    private fun setData() {

        viewModel.contact.observe(this) {
            it?.let {
                binding.editNameEditText.setText(it.name)
                binding.editPhoneNumberEditText.setText(it.phoneNumber)
                binding.editEmailEditText.setText(it.email)
                binding.editGenderEditText.setText(it.gender)
                binding.editRelationEditText.setText(it.relation)
            }
        }
    }

    private fun receiveId(): Long {
        return intent.getLongExtra(CONTACT_ID, -1)
    }
}