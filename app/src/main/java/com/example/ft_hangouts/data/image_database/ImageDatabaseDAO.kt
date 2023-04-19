package com.example.ft_hangouts.data.image_database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build

class ImageDatabaseDAO(private val context: Context) {
    fun getImageFromUri(uri: Uri?): Drawable {
        val imageUri = uri ?: throw IllegalArgumentException("cannot handle null in ImageDatabaseDAO")
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: throw IllegalStateException("cannot translate without imageStream")
        val bitmapDrawable = BitmapDrawable.createFromStream(inputStream, null)

        inputStream.close()

        return bitmapDrawable!!
    }
}