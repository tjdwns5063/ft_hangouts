package com.example.ft_hangouts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.database.ContactContract
import com.example.ft_hangouts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dbHelper = App.contactDbHelper
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val adapter = ContactRecyclerAdapter(getAllItem())

        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val addContactActivityResultLauncher = makeAddContactActivityResultLauncher(adapter)

        binding.button.setOnClickListener {
            addContactActivityResultLauncher.launch(Intent(this, ContactAddActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    private fun makeAddContactActivityResultLauncher(adapter: ContactRecyclerAdapter): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i("data", "SUCCESS")
                val rowId = result?.data?.getLongExtra("data", -1L)
                rowId?.let {
                    if (it < 0L)
                        return@let
                    getLastItem(rowId)
                    adapter.addItem(contactList)
                }
            } else {
                Log.i("data", "FAIL")
            }
        }
    }

    private fun getAllItem(): MutableList<Contact> {
        val readDb = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME_NAME,
            ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER,
            ContactContract.ContactEntry.COLUMN_NAME_GENDER,
            ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
            ContactContract.ContactEntry.COLUMN_NAME_RELATION
        )

        val cursor = readDb.query(ContactContract.ContactEntry.TABLE_NAME, projection, null, null, null, null, null)
        var idx = 0
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME))
                val phoneNumber = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER))
                val email = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL))
                val relation = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION))
                val gender = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))

                if (idx >= contactList.size) {
                    contactList += Contact(id, name, phoneNumber, email, relation, gender)
                }
                ++idx
            }
        }
        return contactList
    }

    fun getLastItem(rowId: Long) {
        val readDb = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME_NAME,
            ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER,
            ContactContract.ContactEntry.COLUMN_NAME_GENDER,
            ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
            ContactContract.ContactEntry.COLUMN_NAME_RELATION
        )

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(rowId.toString())

        val cursor = readDb.query(ContactContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME))
                val phoneNumber = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER))
                val email = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL))
                val relation = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION))
                val gender = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))

                contactList += Contact(id, name, phoneNumber, email, relation, gender)
            }
        }
    }
}