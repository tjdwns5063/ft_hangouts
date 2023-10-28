package com.example.ft_hangouts.data.contact_database

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ft_hangouts.R

class Profile(bitmap: Bitmap? = null): CustomTarget<Bitmap>() {
    private var _bitmap: Bitmap? = bitmap
    val bitmap: Bitmap?
        get() = _bitmap
    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        _bitmap = resource
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        _bitmap?.recycle()
    }
}