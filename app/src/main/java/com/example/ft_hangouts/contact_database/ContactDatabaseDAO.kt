package com.example.ft_hangouts.contact_database

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.ft_hangouts.BackgroundHelper

class ContactDatabaseDAO {
    private val dbHelper: ContactHelper = ContactHelper.createDatabase()
    private val backgroundHelper = BackgroundHelper
//    fun getAllItems(): List<Contact>? {
//        return try {
//            backgroundHelper.execute {
//                dbHelper.getAllItems()
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun getItemById(rowId: Long): Contact? {
//        return try {
//            backgroundHelper.execute {
//                dbHelper.getItemById(rowId)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun addItem(contact: Contact): Long? {
//        return try {
//            backgroundHelper.execute {
//                dbHelper.addItem(contact)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun deleteById(rowId: Long): Int? {
//        return try {
//            backgroundHelper.execute {
//                dbHelper.deleteById(rowId)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun updateById(rowId: Long, contact: Contact): Int? {
//        return try {
//            backgroundHelper.execute {
//                dbHelper.updateById(rowId, contact)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }

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
                ContactContract.ContactEntry.COLUMN_NAME_RELATION
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

                    list += Contact(id, name, phoneNumber, email, relation, gender)
                }
            }
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
            }
            val newRowId = writeDb.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
            return newRowId
        }
    }

    fun updateById(rowId: Long, contact: Contact): Int {
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

            return writeDb.update(
                ContactContract.ContactEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )
        }
    }
}




