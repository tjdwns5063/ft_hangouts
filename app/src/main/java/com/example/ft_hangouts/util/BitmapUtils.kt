package com.example.ft_hangouts.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ft_hangouts.App
import java.io.ByteArrayOutputStream

fun compressBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
    bitmap ?: return null

    val stream: ByteArrayOutputStream = ByteArrayOutputStream()
    val imageViewSize = 128 // dp
    val imageViewSizeToPixel = (imageViewSize * App.INSTANCE.applicationContext.resources.displayMetrics.density).toInt()

    val newBitmap = Bitmap.createScaledBitmap(bitmap, imageViewSizeToPixel, imageViewSizeToPixel, false)
    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

    return stream.toByteArray()
}

fun decodeByteArrayToBitmap(blob: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(blob, 0, blob.size)
}