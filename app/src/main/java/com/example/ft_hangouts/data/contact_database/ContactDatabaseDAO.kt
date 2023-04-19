package com.example.ft_hangouts.data.contact_database

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import com.example.ft_hangouts.BackgroundHelper
import java.io.ByteArrayOutputStream

class ContactDatabaseDAO {
    private val dbHelper: ContactHelper = ContactHelper.createDatabase()

    fun closeDatabase() {
        dbHelper.close()
    }

    fun getAllItems(): List<Contact> {
        with(dbHelper) {
            val readDb = readableDatabase

            val projection = arrayOf(
                BaseColumns._ID,
                ContactContract.ContactEntry.COLUMN_NAME_NAME,
                ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER,
                ContactContract.ContactEntry.COLUMN_NAME_GENDER,
                ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
                ContactContract.ContactEntry.COLUMN_NAME_RELATION,
                ContactContract.ContactEntry.COLUMN_PROFILE
            )

            val cursor = readDb.query(
                ContactContract.ContactEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )
            val list = mutableListOf<Contact>()
            with(cursor) {
                while (moveToNext()) {
                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val name =
                        getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME))
                    val phoneNumber =
                        getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER))
                    val email =
                        getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL))
                    val relation =
                        getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION))
                    val gender =
                        getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))
                    val profile =
                        getBlob(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PROFILE))
                    if (profile != null)
                        println(profile)

                    list += Contact(id, name, phoneNumber, email, relation, gender, profile)
                }
            }
            println(list)
            return list
        }
    }

    fun getItemById(rowId: Long): Contact {
        with(dbHelper) {
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

            val cursor = readDb.query(
                ContactContract.ContactEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            with(cursor) {
                if (!moveToNext()) throw IllegalStateException("can't find contact by id")
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
    }
    fun deleteById(rowId: Long): Int {
        with(dbHelper) {
            val db = writableDatabase

            val selection = "${BaseColumns._ID} LIKE ?"
            val selectionArgs = arrayOf("$rowId")
            val deletedRows =
                db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
            if (deletedRows < 0) throw IllegalStateException("can't delete this rowId $rowId")
            return deletedRows
        }
    }

    fun addItem(contact: Contact): Long {
        with(dbHelper) {
            val writeDb = writableDatabase

            val values = ContentValues().apply {
                put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.name)
                put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.email)
                put(ContactContract.ContactEntry.COLUMN_NAME_GENDER, contact.gender)
                put(ContactContract.ContactEntry.COLUMN_NAME_RELATION, contact.relation)
                put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, contact.phoneNumber)
                put(ContactContract.ContactEntry.COLUMN_PROFILE, contact.profile)
            }
            val newRowId = writeDb.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
            return newRowId
        }
    }

    fun updateById(rowId: Long, contact: Contact) {
        with(dbHelper) {
            val writeDb = writableDatabase

            val values = ContentValues().apply {
                put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.name)
                put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, contact.phoneNumber)
                put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.email)
                put(ContactContract.ContactEntry.COLUMN_NAME_GENDER, contact.gender)
                put(ContactContract.ContactEntry.COLUMN_NAME_RELATION, contact.relation)
            }
            val selection = "${BaseColumns._ID} LIKE ?"
            val selectionArgs = arrayOf(rowId.toString())
            val ret = writeDb.update(
                ContactContract.ContactEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            if (ret == 0)
                throw IllegalStateException("can't update this rowId $rowId")
        }
    }

    companion object {
        fun compressBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            bitmap ?: return null

            val stream: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            return stream.toByteArray()
        }

        fun decodeByteArrayToBitmap(blob: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(blob, 0, blob.size)
        }
    }
}




