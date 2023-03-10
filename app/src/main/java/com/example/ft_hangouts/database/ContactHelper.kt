package com.example.ft_hangouts.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


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

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "contact_db"
    }
}