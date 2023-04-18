package com.example.ft_hangouts.data

import android.content.Context
import com.example.ft_hangouts.App
import java.text.SimpleDateFormat
import java.util.*

object SharedPreferenceUtils {
    enum class SharedPreferenceType {
        APP_BAR_COLOR,
        BACKGROUND_TIME,
        LANGUAGE
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

        return sharedPreference.getInt("color", 16119285)
    }

    fun setLanguage(localeString: String?) {
        val sharedPreference = App.INSTANCE.getSharedPreferences(
            SharedPreferenceType.LANGUAGE.name,
            Context.MODE_PRIVATE
        )

        sharedPreference.edit().putString("locale", localeString).apply()
    }

    fun getLanguage(): String? {
        val sharedPreference = App.INSTANCE.getSharedPreferences(
            SharedPreferenceType.LANGUAGE.name,
            Context.MODE_PRIVATE
        )

        return sharedPreference.getString("locale", null)
    }
}