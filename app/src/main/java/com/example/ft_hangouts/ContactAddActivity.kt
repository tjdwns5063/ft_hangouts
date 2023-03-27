package com.example.ft_hangouts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.ft_hangouts.database.*
import com.example.ft_hangouts.databinding.ActivityContactAddBinding
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class ContactAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactAddBinding
    private val contactDAO = ContactDatabaseDAO()
    private var result: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFocusChangeListener()
        setClickListener()
    }

    private fun setClickListener() {
        binding.addOkButton.setOnClickListener {
            if (!checkEditText()) {
                Toast.makeText(this, "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = Contact(
                id = 0,
                name = binding.addNameEditText.text.toString(),
                email = binding.addEditEmailText.text.toString(),
                phoneNumber = binding.addEditPhoneNumberText.text.toString(),
                gender = binding.addGenderEditText.text.toString(),
                relation = binding.addRelationEditText.text.toString()
            )
            contactDAO.addItem(contact)?.let {
                val intent = Intent(this, MainActivity::class.java)
                    .apply { putExtra("data", result) }
                setResult(Activity.RESULT_OK, intent)
                finish()
            } ?: run {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

        binding.addCancelButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    private fun checkEditText(): Boolean {
        return binding.addNameEditText.text.isNotBlank() && binding.addEditPhoneNumberText.text.isNotBlank()
    }

    private fun setFocusChangeListener() {
        binding.addNameEditText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addNameImage, b)
        }

        binding.addEditPhoneNumberText.setOnFocusChangeListener { view, b ->
            tintRelateImage(binding.addPhoneNumberImage, b)
        }

        binding.addEditEmailText.setOnFocusChangeListener { view, b ->
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