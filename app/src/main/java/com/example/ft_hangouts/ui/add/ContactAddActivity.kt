package com.example.ft_hangouts.ui.add

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactAddBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import kotlinx.coroutines.launch

class ContactAddActivity : BaseActivity() {
    private lateinit var binding: ActivityContactAddBinding
    private val viewModel by lazy {
        ContactAddViewModel(
            ContactDatabaseDAO(ContactHelper.createDatabase(applicationContext)),
            lifecycleScope,
            baseViewModel,
            ImageDatabaseDAO(this))
    }
    private val imageActivityLauncher: ActivityResultLauncher<Intent> by lazy {
        registerImageActivityLauncher()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBinding()
        setContentView(binding.root)
        binding.addProfileImage.clipToOutline = true
        collectAndInitiateProfileImage()
        setFocusChangeListener()
        setClickListener()
        setBackPressedDispatcher()
    }

    private fun setDataBinding() {
        binding = ActivityContactAddBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
    }

    private fun collectAndInitiateProfileImage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileImage.collect {
                    it.bitmapDrawable?.let { bitmapDrawable ->
                        binding.addProfileImage.setImageDrawable(bitmapDrawable)
                    }
                }
            }
        }
    }

    private fun registerImageActivityLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult()) { result: ActivityResult? ->
            val uriString = result?.data?.dataString ?: return@registerForActivityResult

            viewModel.updateProfile(uriString)
        }
    }

    private fun setFocusChangeListener() {
        binding.addNameEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addNameImage, b)
        }

        binding.addPhoneNumberEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addPhoneNumberImage, b)
        }

        binding.addEmailEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addEmailImage, b)
        }

        binding.addGenderEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addGenderImage, b)
        }

        binding.addRelationEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addRelationImage, b)
        }
    }

    private fun tintRelateImage(imageView: ImageView, isFocus: Boolean) {
        when (isFocus) {
            true -> {
                imageView.imageTintList = ColorStateList.valueOf(getColor(R.color.teal_200))
            }
            else -> {
                imageView.imageTintList = ColorStateList.valueOf(getColor(R.color.contact_image_default_color))
            }
        }
    }

    private fun setClickListener() {
        binding.addProfileImage.setOnClickListener { onProfileImageClick() }

        binding.addOkButton.setOnClickListener { onOkButtonClick() }

        binding.addCancelButton.setOnClickListener { onCancelButtonClick() }
    }

    private fun onProfileImageClick() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }

        imageActivityLauncher.launch(intent)
    }

    private fun onOkButtonClick() {
        if (!checkEditText()) {
            Toast.makeText(this, getString(R.string.empty_name_or_phone), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.addContact(
            name = binding.addNameEditText.text.toString(),
            email = binding.addEmailEditText.text.toString(),
            phoneNumber = binding.addPhoneNumberEditText.text.toString(),
            gender = binding.addGenderEditText.text.toString(),
            relation = binding.addRelationEditText.text.toString()
        )
    }

    private fun checkEditText(): Boolean {
        return binding.addNameEditText.text.isNotBlank() && binding.addPhoneNumberEditText.text.isNotBlank()
    }

    private fun onCancelButtonClick() {
        finish()
    }

    private fun setBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}