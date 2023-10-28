package com.example.ft_hangouts.ui.add

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactAddBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import kotlinx.coroutines.launch

class ContactAddActivity : BaseActivity() {
    private lateinit var binding: ActivityContactAddBinding
    private val viewModel: ContactAddViewModel by viewModels {
        AddViewModelFactory(ContactDatabase.INSTANCE,
        baseViewModel,
        ImageDatabaseDAO(this))
    }
    private lateinit var imageActivityLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBinding()
        setContentView(binding.root)
        imageActivityLauncher = registerImageActivityLauncher()
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
                    it.bitmap?.let { bitmap ->
                        binding.addProfileImage.scaleType = ImageView.ScaleType.FIT_XY
                        binding.addProfileImage.setImageDrawable(BitmapDrawable(null, bitmap))
                    } ?: run {
                        binding.addProfileImage.setImageResource(R.drawable.baseline_camera_alt_24)
                        binding.addProfileImage.scaleType = ImageView.ScaleType.CENTER
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
        binding.addNameEditText.setOnFocusChangeListener { _, b ->
            tintRelateImage(binding.addNameImage, b)
        }

        binding.addPhoneNumberEditText.setOnFocusChangeListener { _, b ->
            tintRelateImage(binding.addPhoneNumberImage, b)
        }

        binding.addEmailEditText.setOnFocusChangeListener { _, b ->
            tintRelateImage(binding.addEmailImage, b)
        }

        binding.addGenderEditText.setOnFocusChangeListener { _, b ->
            tintRelateImage(binding.addGenderImage, b)
        }

        binding.addRelationEditText.setOnFocusChangeListener { _, b ->
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

        binding.deleteProfileImage.setOnClickListener { viewModel.clearProfileImage() }
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