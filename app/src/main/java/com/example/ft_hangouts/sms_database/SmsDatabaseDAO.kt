package com.example.ft_hangouts.sms_database

import android.content.ContentValues
import android.provider.BaseColumns
import java.util.concurrent.Executors
import com.example.ft_hangouts.sms_database.SmsContract.SmsEntry
import java.util.concurrent.Callable

class SmsDatabaseDAO {
    private val smsDatabaseHelper = SmsDatabaseHelper.createDatabase()
    private val executor = Executors.newFixedThreadPool(8)

    fun closeDatabase() {
        smsDatabaseHelper.close()
    }

    fun getAllItems(): List<Sms>? {
        val callable = Callable {
            smsDatabaseHelper.getAllItems()
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    fun addItem(sms: Sms): Long? {
        val callable = Callable {
            smsDatabaseHelper.addItem(sms)
        }
        val future = executor.submit(callable)
        return try {
            future.get()
        } catch (e: Exception) {
            null
        }
    }

    private fun SmsDatabaseHelper.getAllItems(): List<Sms> {
        val readDb = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            SmsEntry.COLUMN_NAME_TO_PERSON,
            SmsEntry.COLUMN_NAME_FROM_PERSON,
            SmsEntry.COLUMN_NAME_CONTENT,
            SmsEntry.COLUMN_NAME_DATE,
        )

        val cursor = readDb.query(SmsEntry.TABLE_NAME, projection, null, null, null, null, null)
        val list = mutableListOf<Sms>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val to = getString(getColumnIndexOrThrow(SmsEntry.COLUMN_NAME_TO_PERSON))
                val from = getString(getColumnIndexOrThrow(SmsEntry.COLUMN_NAME_FROM_PERSON))
                val content = getString(getColumnIndexOrThrow(SmsEntry.COLUMN_NAME_CONTENT))
                val date = getLong(getColumnIndexOrThrow(SmsEntry.COLUMN_NAME_DATE))

                list += Sms(id, to, from, content, date)
            }
        }
        return list
    }

    private fun SmsDatabaseHelper.addItem(sms: Sms): Long? {
        val writeDb = writableDatabase

        val values = ContentValues().apply {
            put(SmsEntry.COLUMN_NAME_TO_PERSON, sms.to)
            put(SmsEntry.COLUMN_NAME_FROM_PERSON, sms.from)
            put(SmsEntry.COLUMN_NAME_CONTENT, sms.content)
            put(SmsEntry.COLUMN_NAME_DATE, sms.date)
        }
        val newRowId = writeDb?.insert(SmsEntry.TABLE_NAME, null, values)
        return newRowId
    }

//    private fun ContactHelper.getItemById(rowId: Long): Contact? {
//        val readDb = readableDatabase
//
//        val projection = arrayOf(
//            BaseColumns._ID,
//            ContactContract.ContactEntry.COLUMN_NAME_NAME,
//            ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER,
//            ContactContract.ContactEntry.COLUMN_NAME_GENDER,
//            ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
//            ContactContract.ContactEntry.COLUMN_NAME_RELATION
//        )
//
//        val selection = "${BaseColumns._ID} = ?"
//        val selectionArgs = arrayOf(rowId.toString())
//
//        val cursor = readDb.query(ContactContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
//        with(cursor) {
//            if (moveToNext()) {
//                return Contact(
//                    id = getLong(getColumnIndexOrThrow(BaseColumns._ID)),
//                    name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_NAME)),
//                    phoneNumber = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER)),
//                    email = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_EMAIL)),
//                    relation = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_RELATION)),
//                    gender = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME_GENDER))
//                )
//            }
//        }
//        return null
//    }
//    private fun ContactHelper.deleteById(rowId: Long): Int {
//        val db = writableDatabase
//
//        val selection = "${BaseColumns._ID} LIKE ?"
//        val selectionArgs = arrayOf("$rowId")
//        val deletedRows = db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
//        return deletedRows
//    }
//

}