package com.example.ft_hangouts.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.ft_hangouts.R


//@BindingAdapter("profile")
//fun profile(imageView: ImageView, contact: Contact?) {
//    val uri = Uri.parse(contact?.profile ?: "")
//    val scaleType = contact?.profile?.let{
//        ImageView.ScaleType.FIT_XY
//    } ?: ImageView.ScaleType.CENTER
//    println("profile bind called ${contact?.profile}")
//    imageView.scaleType = scaleType
////    Glide.with(imageView).asBitmap().load(uri).into(Target)
//    Glide.with(imageView.context).load(uri).error(R.drawable.baseline_man_4_32).into(imageView)
//}

@BindingAdapter("profile")
fun profile(imageView: ImageView, bitmap: Bitmap?) {
    val scaleType = bitmap?.let{
        ImageView.ScaleType.FIT_XY
    } ?: ImageView.ScaleType.CENTER
    println("profile bind called $bitmap")
    imageView.scaleType = scaleType
    Glide.with(imageView.context).load(bitmap).error(R.drawable.baseline_man_4_32).into(imageView)
}

@BindingAdapter("profile")
fun profile(imageView: ImageView, raw: ByteArray?) {
    val scaleType = raw?.let{
        ImageView.ScaleType.FIT_XY
    } ?: ImageView.ScaleType.CENTER
    println("profile raw called $raw")

    val bitmap = raw?.let {
        BitmapFactory.decodeByteArray(it, 0, it.size)
    }

    if (bitmap == null) {
        Glide.with(imageView).load(R.drawable.baseline_man_4_32).into(imageView)
    } else {
        Glide.with(imageView.context).load(bitmap).error(R.drawable.baseline_man_4_32).into(imageView)
    }
    imageView.scaleType = scaleType
}