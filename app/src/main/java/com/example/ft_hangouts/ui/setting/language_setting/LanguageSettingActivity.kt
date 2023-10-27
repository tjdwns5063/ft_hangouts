package com.example.ft_hangouts.ui.setting.language_setting

import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityLanguageSettingBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.main.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class LanguageSettingActivity : BaseActivity() {
    private val binding by lazy { ActivityLanguageSettingBinding.inflate(layoutInflater) }
    private val viewModel: LanguageSettingViewModel by viewModels { LanguageViewModelFactory(sharedPreferenceUtils) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedLanguage.collect {
                    setInitialLanguage(it)
                }
            }
        }

        binding.languageBackButton.setOnClickListener { finish() }
        binding.languageEnCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
        }

        binding.languageDefaultCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
        }
        binding.languageKrCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
        }
    }

    private fun setInitialLanguage(language: String?) {
        when(language) {
            "ko" -> binding.languageKrCheckbox.isChecked = true
            "en" -> binding.languageEnCheckbox.isChecked = true
            else -> binding.languageDefaultCheckbox.isChecked = true
        }
    }

    private fun cancelSelectionPrevLanguage(view: View) {
        val languageCheckBoxes = arrayOf(
            binding.languageDefaultCheckbox,
            binding.languageKrCheckbox,
            binding.languageEnCheckbox
        )

        for (checkBox in languageCheckBoxes) {
            if (checkBox.isChecked && checkBox != view) {
                checkBox.isChecked = false
            }
        }
    }

    private fun matchViewToLocaleList(view: View): LocaleList {
        return when(view) {
            binding.languageKrCheckbox -> LocaleList(Locale.KOREAN)
            binding.languageEnCheckbox -> LocaleList(Locale.ENGLISH)
            else -> LocaleList.getEmptyLocaleList()
        }
    }

    private fun changeLanguage(view: View) {
        val localeList = matchViewToLocaleList(view)
        val localeString = if (localeList.isEmpty) null else localeList[0].language

        viewModel.updateLanguage(localeString)
        if (Build.VERSION.SDK_INT >= 33) {
            val localManager = getSystemService(LocaleManager::class.java)

            localManager.applicationLocales = localeList
        } else {
            EventDialog.showEventDialog(
                fragmentManager = supportFragmentManager,
                message = getString(R.string.language_change_message),
                onClick = { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
            })
        }
    }
}