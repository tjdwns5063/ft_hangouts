package com.example.ft_hangouts.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.App
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity: AppCompatActivity() {
    protected val baseViewModel by lazy { BaseViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseViewModel.errorHandler.observe(this) {
            it ?: return@observe

            if (it.isSuccess()) {
                finish()
            } else {
                it.handle(this)
            }
        }
    }
    override fun onPause() {
        super.onPause()

        val app = application as App
        app.sendResume(System.currentTimeMillis())
    }

    override fun onResume() {
        super.onResume()

        val currClassName = this.javaClass.name
        val app = application as App

        app.showBackgroundTime(currClassName)
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.let {
            val localeString = SharedPreferenceUtils.getLanguage()
            println(localeString)
            val configuration = it.resources.configuration
            val locale = if (localeString == null) null else Locale(localeString)

            configuration.setLocale(locale)
            val context = it.createConfigurationContext(configuration)
            super.attachBaseContext(context)
        } ?: super.attachBaseContext(null)
    }
}