package com.example.ft_hangouts.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.App
import com.example.ft_hangouts.data.SharedPreferenceUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

object ContactActivityContract {
    const val CONTACT_ID = "contactId"
}
open class BaseActivity : AppCompatActivity() {
    protected val baseViewModel by lazy { BaseViewModel(lifecycleScope) }
    protected val sharedPreferenceUtils by lazy { SharedPreferenceUtils(App.INSTANCE.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                baseViewModel.errorHandler.collect {
                    it ?: return@collect

                    if (it.isSuccess()) {
                        finish()
                    } else {
                        it.handle(baseContext)
                        baseViewModel.initiateError()
                    }
                }
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
        val localeString = sharedPreferenceUtils.getLanguage()
        val configuration = context.resources.configuration
        val locale = if (localeString == null) null else Locale(localeString)

        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}