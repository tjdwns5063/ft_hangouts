package com.example.ft_hangouts.ui.add

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactAddBinding
import com.example.ft_hangouts.ui.BaseActivity

class ContactAddActivity : BaseActivity() {
    private lateinit var binding: ActivityContactAddBinding
    private val viewModel by lazy { ContactAddViewModel(lifecycleScope, baseViewModel, ImageDatabaseDAO(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_contact_add, null, false)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        binding.addProfileImage.clipToOutline = true
        val imageActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                val uri = result?.data?.data ?: return@registerForActivityResult

                viewModel.updateProfile(uri)
            }
        viewModel.profileImage.observe(this) {
            it?.let { binding.addProfileImage.setImageDrawable(it) }
        }
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        binding.addProfileImage.setOnClickListener { imageActivityLauncher.launch(intent) }
        setFocusChangeListener()
        setClickListener()
    }

    private fun setClickListener() {
        binding.addOkButton.setOnClickListener {
            onOkButtonClick()
        }

        binding.addCancelButton.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun onOkButtonClick() {
        if (!checkEditText()) {
            Toast.makeText(this, "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
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
}