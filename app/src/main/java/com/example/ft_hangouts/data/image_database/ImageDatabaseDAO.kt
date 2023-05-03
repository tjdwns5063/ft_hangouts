package com.example.ft_hangouts.data.image_database

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import com.example.ft_hangouts.ui.edit.Profile

class ImageDatabaseDAO(private val context: Context) {
    fun getImageFromUri(uriString: String): Profile {
        val imageUri = parseImageUri(uriString)
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalStateException("cannot translate without imageStream")
        val bitmapDrawable = BitmapDrawable.createFromStream(inputStream, "") as BitmapDrawable

        inputStream.close()

        return Profile(bitmapDrawable)
    }

    private fun parseImageUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}