package com.example.ft_hangouts.ui.edit

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactEditBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import kotlinx.coroutines.launch

class ContactEditActivity : BaseActivity() {
    private val id by lazy { receiveId() }
    private val binding by lazy { ActivityContactEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ContactEditViewModel(
            ContactDatabaseDAO(ContactHelper.createDatabase(applicationContext)),
            id,
            lifecycleScope,
            super.baseViewModel,
            ImageDatabaseDAO(this)
        ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageSelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uriString = it.data?.dataString ?: return@registerForActivityResult

                viewModel.updateProfileImage(uriString)
            }
        }

        binding.editProfileImage.clipToOutline = true

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updatedProfile.collect {
                    it.bitmapDrawable?.let { bitmapDrawable ->
                        binding.editProfileImage.setImageDrawable(bitmapDrawable)
                    } ?: run { binding.editProfileImage.setImageResource(R.drawable.ic_default_profile) }
                }
            }
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
            Toast.makeText(this, getString(R.string.empty_name_or_phone), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.updateContact(
            name = binding.editNameEditText.text.toString(),
            email = binding.editEmailEditText.text.toString(),
            phoneNumber = binding.editPhoneNumberEditText.text.toString(),
            gender = binding.editGenderEditText.text.toString(),
            relation = binding.editRelationEditText.text.toString()
        )
    }

    private fun setData() {
        lifecycleScope.launch {
            viewModel.contact.collect {
                binding.editNameEditText.setText(it.name)
                binding.editPhoneNumberEditText.setText(it.phoneNumber)
                binding.editEmailEditText.setText(it.email)
                binding.editGenderEditText.setText(it.gender)
                binding.editRelationEditText.setText(it.relation)
                it.profile?.let { bitmap ->
                    binding.editProfileImage.setImageBitmap(bitmap)
                } ?: run { binding.editProfileImage.setImageResource(R.drawable.ic_default_profile) }
            }
        }
    }

    private fun receiveId(): Long {
        return intent.getLongExtra(CONTACT_ID, -1)
    }
}