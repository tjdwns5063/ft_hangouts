package com.example.ft_hangouts.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ft_hangouts.App
import com.example.ft_hangouts.data.SharedPreferenceUtils
import java.util.*

object ContactActivityContract {
    const val CONTACT_ID = "contactId"
}
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

        (application as App).sendResume(System.currentTimeMillis())
    }

    override fun onResume() {
        super.onResume()

        (application as App).showBackgroundTime(this.javaClass.name)
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.let {
            super.attachBaseContext(createLanguageSettingContext(newBase))
        } ?: super.attachBaseContext(null)
    }

    private fun createLanguageSettingContext(context: Context): Context {
        val localeString = SharedPreferenceUtils.getLanguage()
        val configuration = context.resources.configuration
        val locale = if (localeString == null) null else Locale(localeString)

        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}