package com.example.ft_hangouts.data.contact_database

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.example.ft_hangouts.R

data class Profile(val bitmapDrawable: BitmapDrawable?) {
    fun changeScaleType(view: ImageView) {
        bitmapDrawable?.let {
            view.scaleType = ImageView.ScaleType.FIT_XY
            view.setImageDrawable(bitmapDrawable)
        } ?: run {
            view.scaleType = ImageView.ScaleType.CENTER
            view.setImageResource(R.drawable.baseline_camera_alt_24)
        }
    }

    companion object {
        fun changeScaleType(view: ImageView, bitmap: Bitmap?) {
            bitmap?.let {
                view.scaleType = ImageView.ScaleType.FIT_XY
                view.setImageBitmap(bitmap)
            } ?: run {
                view.scaleType = ImageView.ScaleType.CENTER
                view.setImageResource(R.drawable.baseline_camera_alt_24)
            }
        }
    }
}