package com.example.ft_hangouts

import android.content.Context
import androidx.room.Room
import com.example.ft_hangouts.data.contact_database.ContactDatabase

fun createContactDatabase(context: Context): ContactDatabase {
    return Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
}
