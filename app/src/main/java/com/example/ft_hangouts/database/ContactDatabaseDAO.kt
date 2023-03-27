package com.example.ft_hangouts.database

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.ft_hangouts.App
import java.util.concurrent.Callable
import java.util.concurrent.Executors


class ContactDatabaseDAO {
    private val contactHelper = ContactHelper.createDatabase()
    private val executor = Executors.newFixedThreadPool(8)
    fun getAllItems(): List<Contact>? {
        val callable = Callable {
            contactHelper.getAllItems()
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    fun getItemById(rowId: Long): Contact? {
        val callable = Callable {
            contactHelper.getItemById(rowId)
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    fun addItem(contact: Contact): Long? {
        val callable = Callable {
            contactHelper.addItem(contact)
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    fun deleteById(rowId: Long): Int? {
        val callable = Callable {
            contactHelper.deleteById(rowId)
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    fun closeDatabase() {
        contactHelper.close()
    }

    private fun ContactHelper.getAllItems(): List<Contact> {
        val readDb = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME_NAME,
            ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER,
            ContactContract.ContactEntry.COLUMN_NAME_GENDER,
            ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
            ContactContract.ContactEntry.COLUMN_NAME_RELATION
        )

        val cursor = readDb.query(ContactContract.ContactEntry.TABLE_NAME, projection, null, null, null, null, null)
        val list = mutableListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME))
                val phoneNumber = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER))
                val email = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL))
                val relation = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION))
                val gender = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))

                list += Contact(id, name, phoneNumber, email, relation, gender)
            }
        }
        return list
    }

    private fun ContactHelper.getItemById(rowId: Long): Contact? {
        val readDb = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
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
            if (moveToNext()) {
                return Contact(
                    id = getLong(getColumnIndexOrThrow(BaseColumns._ID)),
                    name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME)),
                    phoneNumber = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER)),
                    email = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL)),
                    relation = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION)),
                    gender = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))
                )
            }
        }
        return null
    }
    private fun ContactHelper.deleteById(rowId: Long): Int {
        val db = writableDatabase

        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf("$rowId")
        val deletedRows = db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
        return deletedRows
    }

    private fun ContactHelper.addItem(contact: Contact): Long? {
        val writeDb = writableDatabase

        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.email)
            put(ContactContract.ContactEntry.COLUMN_NAME_GENDER, contact.gender)
            put(ContactContract.ContactEntry.COLUMN_NAME_RELATION, contact.relation)
            put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, contact.phoneNumber)
        }
        val newRowId = writeDb?.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
        return newRowId
    }
}




