package com.example.ft_hangouts.ui.edit

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.ImageDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.Profile
import com.example.ft_hangouts.databinding.ActivityContactEditBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract.CONTACT_ID
import kotlinx.coroutines.launch

class ContactEditActivity : BaseActivity() {
    private val id by lazy { receiveId() }
    private val binding by lazy { ActivityContactEditBinding.inflate(layoutInflater) }
    private val viewModel: ContactEditViewModel by viewModels {
        EditViewModelFactory(
            id,
            super.baseViewModel,
            ContactDatabase.INSTANCE,
            ImageDAO(App.INSTANCE)
        )
    }
    private lateinit var imageSelectLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        imageSelectLauncher = registerImageSelectLauncher()
        binding.editProfileImage.clipToOutline = true
        setClickListener()
        repeatInitViewModel()
    }

    private fun registerImageSelectLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uriString = it.data?.dataString ?: return@registerForActivityResult

                lifecycleScope.launch {
                    viewModel.updateProfileImage(uriString)
                }
            }
        }
    }

    private fun setClickListener() {
        binding.editProfileImage.setOnClickListener {
            imageSelectLauncher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
        }

        binding.editProfileDeleteImage.setOnClickListener {
            viewModel.clearProfileImage()
        }

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

    private fun receiveId(): Long {
        return intent.getLongExtra(CONTACT_ID, -1)
    }

    private fun repeatInitViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.initViewModel()
            }
        }
    }
}