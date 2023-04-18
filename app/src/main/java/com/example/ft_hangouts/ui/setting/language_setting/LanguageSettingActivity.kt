package com.example.ft_hangouts.ui.setting.language_setting

import android.os.Bundle
import com.example.ft_hangouts.databinding.ActivityLanguageSettingBinding
import com.example.ft_hangouts.ui.BaseActivity

class LanguageSettingActivity : BaseActivity() {
    private val binding by lazy { ActivityLanguageSettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}