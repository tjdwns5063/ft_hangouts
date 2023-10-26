package com.example.ft_hangouts.data.contact_database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ft_hangouts.App

@Database(entities = [Contact::class], version = 2)
abstract class ContactDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactDAO
    companion object {
        lateinit var INSTANCE: ContactDatabase

        fun create(applicationContext: Context): ContactDatabase {
            return Room.databaseBuilder(
                applicationContext,
                ContactDatabase::class.java,
                "contact_db"
            ).build()
        }
    }
}
//private const val SQL_CREATE_ENTRIES =
//    "CREATE TABLE ${ContactContract.ContactEntry.TABLE_NAME} (" +
//            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//            "${ContactContract.ContactEntry.COLUMN_PROFILE} BLOB," +
//            "${ContactContract.ContactEntry.COLUMN_NAME_NAME} TEXT," +
//            "${ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER} TEXT," +
//            "${ContactContract.ContactEntry.COLUMN_NAME_GENDER} TEXT," +
//            "${ContactContract.ContactEntry.COLUMN_NAME_RELATION} TEXT," +
//            "${ContactContract.ContactEntry.COLUMN_NAME_EMAIL} TEXT)"
//
//private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ContactContract.ContactEntry.TABLE_NAME}"
//private const val SQL_ALTER_VERSION_2 = "ALTER TABLE ${ContactContract.ContactEntry.TABLE_NAME} ADD COLUMN ${ContactContract.ContactEntry.COLUMN_PROFILE} BLOB"
//
//
//class ContactHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    override fun onCreate(db: SQLiteDatabase?) {
//        db?.execSQL(SQL_CREATE_ENTRIES)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        if (oldVersion < 2) {
//            db?.execSQL(SQL_ALTER_VERSION_2)
//        }
//    }
//
//    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        onUpgrade(db, oldVersion, newVersion)
//    }
//
//    companion object {
//        private const val DATABASE_VERSION = 2
//        private const val DATABASE_NAME = "contact_db"
//        private lateinit var INSTANCE: ContactHelper
//
//        fun createDatabase(application: Context): ContactHelper {
//            synchronized(ContactHelper::class.java) {
//                if (!::INSTANCE.isInitialized) {
//                    INSTANCE = ContactHelper(application)
//                }
//            }
//            return INSTANCE
//        }
//    }
//}