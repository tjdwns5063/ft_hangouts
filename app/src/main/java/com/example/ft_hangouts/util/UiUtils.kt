package com.example.ft_hangouts.ui

import com.example.ft_hangouts.App

fun dpToPixel(dp: Int): Int {
    return (dp * App.INSTANCE.applicationContext.resources.displayMetrics.density).toInt()
}

fun dpToPixel(dp: Float): Float {
    return dp * App.INSTANCE.applicationContext.resources.displayMetrics.density
}
