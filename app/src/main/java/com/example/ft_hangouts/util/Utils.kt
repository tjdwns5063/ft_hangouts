package com.example.ft_hangouts.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.Telephony
import com.example.ft_hangouts.App
import java.io.ByteArrayOutputStream

fun parseSmsMessage(intent: Intent): String {
    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    val receiveMessage = StringBuilder()

    for (smsMessage in messages) {
        receiveMessage.append(smsMessage.messageBody)
    }
    return receiveMessage.toString()
}

fun dpToPixel(dp: Int): Int {
    return (dp * App.INSTANCE.applicationContext.resources.displayMetrics.density).toInt()
}

fun dpToPixel(dp: Float): Float {
    return dp * App.INSTANCE.applicationContext.resources.displayMetrics.density
}

fun compressBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
    bitmap ?: return null

    val stream: ByteArrayOutputStream = ByteArrayOutputStream()
    val imageViewSize = 128 // dp
    val imageViewSizeToPixel = (imageViewSize * App.INSTANCE.applicationContext.resources.displayMetrics.density).toInt()

    val newBitmap =
        Bitmap.createScaledBitmap(bitmap, imageViewSizeToPixel, imageViewSizeToPixel, false)
    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

    return stream.toByteArray()
}

fun decodeByteArrayToBitmap(blob: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(blob, 0, blob.size)
}