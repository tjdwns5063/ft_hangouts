package com.example.ft_hangouts.ui

import com.example.ft_hangouts.App

fun pixelToDp(pixel: Int): Int {
    return (pixel * App.INSTANCE.applicationContext.resources.displayMetrics.density).toInt()
}

fun pixelToDp(pixel: Float): Float {
    return pixel * App.INSTANCE.applicationContext.resources.displayMetrics.density
}