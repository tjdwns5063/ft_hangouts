package com.example.ft_hangouts.ui.setting.language_setting

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.LocaleList
import android.view.View
import com.example.ft_hangouts.EventDialog
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.databinding.ActivityLanguageSettingBinding
import com.example.ft_hangouts.ui.BaseActivity
import com.example.ft_hangouts.ui.main.MainActivity
import java.util.*

class LanguageSettingActivity : BaseActivity() {
    private val binding by lazy { ActivityLanguageSettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.languageBackButton.setOnClickListener { finish() }
        binding.languageEnCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
        }
        binding.languageDefaultCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
        }
        binding.languageKrCheckbox.setOnClickListener {
            cancelSelectionPrevLanguage(it)
            cancelSelectionPrevLanguage(it)
            changeLanguage(it)
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

        if (Build.VERSION.SDK_INT >= 33) {
            val localManager = getSystemService(LocaleManager::class.java)

            localManager.applicationLocales = localeList
        } else {
            val localeString = if (localeList.isEmpty) null else localeList[0].language
            sharedPreferenceUtils.setLanguage(localeString)

            EventDialog.showEventDialog(
                fragmentManager = supportFragmentManager,
            message = "언어를 변경하려면 앱을 재시작해야합니다. 재시작 하시겠습니까?",
            onClick = { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            })
        }
    }
}