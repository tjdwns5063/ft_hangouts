package com.example.ft_hangouts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ft_hangouts.database.ContactContract
import com.example.ft_hangouts.databinding.ActivityContactAddBinding

class ContactAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactAddBinding
    private val dbHelper = App.contactDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFocusChangeListener()
        setClickListener()
    }

    private fun setClickListener() {
        binding.addOkButton.setOnClickListener {
            val result = addItem()

            if (result == null) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("data", result)
                }
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }

        binding.addCancelButton.setOnClickListener {
            finish()
        }
    }

    private fun addItem(): Long? {
        val writeDb = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME_NAME, binding.addNameEditText.text.toString())
            put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, binding.addEditEmailText.text.toString())
            put(ContactContract.ContactEntry.COLUMN_NAME_GENDER, binding.addGenderEditText.text.toString())
            put(ContactContract.ContactEntry.COLUMN_NAME_RELATION, binding.addRelationEditText.text.toString())
            put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, binding.addEditPhoneNumberText.text.toString())
        }
        val newRowId = writeDb?.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
        return newRowId
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
                imageView.imageTintList = ColorStateList.valueOf(getColor(R.color.teal_700))
            }
            else -> {
                imageView.imageTintList = ColorStateList.valueOf(getColor(R.color.contact_image_default_color))
            }
        }
    }
}