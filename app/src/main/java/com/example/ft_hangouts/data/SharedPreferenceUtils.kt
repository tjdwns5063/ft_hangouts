package com.example.ft_hangouts.data

import android.content.Context
import com.example.ft_hangouts.App
import java.text.SimpleDateFormat
import java.util.*

object SharedPreferenceUtils {
    enum class SharedPreferenceType {
        APP_BAR_COLOR,
        BACKGROUND_TIME
    }

    fun setAppbarColor(color: Int) {
        val sharedPreference = App.INSTANCE.getSharedPreferences(
            SharedPreferenceType.APP_BAR_COLOR.name,
            Context.MODE_PRIVATE
        )

        sharedPreference.edit().putInt("color", color).apply()
    }

    fun getAppbarColor(): Int {
        val sharedPreference = App.INSTANCE.getSharedPreferences(
            SharedPreferenceType.APP_BAR_COLOR.name,
            Context.MODE_PRIVATE
        )

        return sharedPreference.getInt("color", Int.MIN_VALUE)
    }
}