package com.example.ft_hangouts.sms_database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.ft_hangouts.App
import com.example.ft_hangouts.sms_database.SmsContract.SmsEntry

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${SmsEntry.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
        "${SmsEntry.COLUMN_NAME_TO_PERSON} TEXT," +
        "${SmsEntry.COLUMN_NAME_FROM_PERSON} TEXT," +
        "${SmsEntry.COLUMN_NAME_CONTENT} TEXT," +
        "${SmsEntry.COLUMN_NAME_DATE} TEXT)"


private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${SmsEntry.TABLE_NAME}"

private lateinit var INSTANCE: SmsDatabaseHelper

class SmsDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

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

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "sms_db"

        fun createDatabase(): SmsDatabaseHelper {
            synchronized(SmsDatabaseHelper::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = SmsDatabaseHelper(App.INSTANCE)
                }
            }
            return INSTANCE
        }
    }
}