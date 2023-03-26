package com.example.ft_hangouts.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log


private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${ContactContract.ContactEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ContactContract.ContactEntry.COLUMN_NAME_NAME} TEXT," +
            "${ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER} TEXT," +
            "${ContactContract.ContactEntry.COLUMN_NAME_GENDER} TEXT," +
            "${ContactContract.ContactEntry.COLUMN_NAME_RELATION} TEXT," +
            "${ContactContract.ContactEntry.COLUMN_NAME_EMAIL} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ContactContract.ContactEntry.TABLE_NAME}"


class ContactHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun getAllItems(): List<Contact> {
        val readDb = readableDatabase

        val projection = arrayOf(BaseColumns._ID,
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

    fun getItemById(rowId: Long): Contact? {
        val readDb = readableDatabase

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

    fun addItem(contact: Contact): Long? {
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

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "contact_db"
    }
}