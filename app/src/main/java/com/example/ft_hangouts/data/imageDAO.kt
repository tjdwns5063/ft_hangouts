package com.example.ft_hangouts.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.ft_hangouts.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageDAO(private val context: Context) {
    val density: Float = context.resources.displayMetrics.density

    suspend fun loadBitmap(url: String): Bitmap? {
        val uri = Uri.parse(url)

        val future = Glide.with(context).asBitmap().load(uri).submit()

        val result = try {
            withContext(Dispatchers.IO) {
                future.get()
            }
        } catch (err: InterruptedException) {
            null
        }

        return result
    }
}
