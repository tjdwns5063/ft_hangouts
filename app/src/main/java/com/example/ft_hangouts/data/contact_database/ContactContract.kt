package com.example.ft_hangouts.data.contact_database

import android.provider.BaseColumns

object ContactContract {
    object ContactEntry: BaseColumns {
        const val TABLE_NAME = "contact"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_PHONE_NUMBER = "phone_number"
        const val COLUMN_NAME_GENDER = "gender"
        const val COLUMN_NAME_RELATION = "relation"
        const val COLUMN_NAME_EMAIL = "email"
    }
}