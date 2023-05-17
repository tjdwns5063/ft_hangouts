package com.example.ft_hangouts.data

import android.content.Context

class SharedPreferenceUtils(private val context: Context) {
    enum class SharedPreferenceType {
        APP_BAR_COLOR,
        LANGUAGE
    }

    fun setAppbarColor(color: Int) {
        val sharedPreference = context.getSharedPreferences(
            SharedPreferenceType.APP_BAR_COLOR.name,
            Context.MODE_PRIVATE
        )

        sharedPreference.edit().putInt("color", color).apply()
    }

    fun getAppbarColor(): Int {
        val sharedPreference = context.getSharedPreferences(
            SharedPreferenceType.APP_BAR_COLOR.name,
            Context.MODE_PRIVATE
        )

        return sharedPreference.getInt("color", -657931)
    }

    fun setLanguage(localeString: String?) {
        val sharedPreference = context.getSharedPreferences(
            SharedPreferenceType.LANGUAGE.name,
            Context.MODE_PRIVATE
        )

        sharedPreference.edit().putString("locale", localeString).apply()
    }

    fun getLanguage(): String? {
        val sharedPreference = context.getSharedPreferences(
            SharedPreferenceType.LANGUAGE.name,
            Context.MODE_PRIVATE
        )

        return sharedPreference.getString("locale", null)
    }
}