package com.example.ft_hangouts.sms_database

import android.provider.BaseColumns

object SmsContract {
    object SmsEntry: BaseColumns {
        const val TABLE_NAME = "Sms"
        const val COLUMN_NAME_TO_PERSON = "to_person"
        const val COLUMN_NAME_FROM_PERSON = "from_person"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_DATE = "date"
    }
}