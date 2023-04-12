package com.example.ft_hangouts.sms
import android.content.ContentResolver
import android.provider.Telephony


class SmsDatabaseDAO(private val contentResolver: ContentResolver) {
    fun getMessage(phoneNumber: String): List<SmsInfo> {
        val numberCol = Telephony.TextBasedSmsColumns.ADDRESS
        val textCol = Telephony.TextBasedSmsColumns.BODY
        val typeCol = Telephony.TextBasedSmsColumns.TYPE
        val dateCol = Telephony.TextBasedSmsColumns.DATE
        val uri = Telephony.Sms.CONTENT_URI

        val projection = arrayOf(numberCol, textCol, typeCol, dateCol)
        val cursor = contentResolver.query(
            uri,
            projection,
            "$numberCol LIKE ?",
            arrayOf(
                "%$phoneNumber%",
            ),
            "$dateCol ASC"
        )

        val textColIdx = cursor!!.getColumnIndex(textCol)
        val typeColIdx = cursor.getColumnIndex(typeCol)
        val dateColIdx = cursor.getColumnIndex(dateCol)
        val smsList = mutableListOf<SmsInfo>()
        while (cursor.moveToNext()) {
            val text = cursor.getString(textColIdx)
            val type = cursor.getInt(typeColIdx)
            val date = cursor.getLong(dateColIdx)

            smsList += SmsInfo(text, date, type)
        }
        cursor.close()
        return smsList
    }
}