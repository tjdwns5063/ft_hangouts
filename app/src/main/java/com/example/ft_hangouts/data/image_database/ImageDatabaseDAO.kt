package com.example.ft_hangouts.data.image_database

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.ft_hangouts.data.contact_database.Profile

class ImageDatabaseDAO(private val context: Context) {
    fun loadImageIntoProfile(uriString: String): Profile {
        val imageUri = parseImageUri(uriString)
        val profile = Profile()

        Glide.with(context).asBitmap().load(imageUri).into(profile)
        return profile
    }

    private fun parseImageUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}