package com.example.ft_hangouts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.database.ContactContract
import com.example.ft_hangouts.databinding.ActivityContactAddBinding

class ContactAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactAddBinding
    val dbHelper = App.contactDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}