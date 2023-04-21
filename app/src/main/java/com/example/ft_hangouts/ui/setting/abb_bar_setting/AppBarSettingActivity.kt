package com.example.ft_hangouts.ui.setting.abb_bar_setting

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.annotation.ColorInt
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityAppBarSettingBinding
import com.example.ft_hangouts.ui.base.BaseActivity

class AppBarSettingActivity : BaseActivity() {
    private val binding by lazy { ActivityAppBarSettingBinding.inflate(layoutInflater) }
    @ColorInt private var selectedColor: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appbarSettingColorPicker.setOnColorListener { color ->
            selectedColor = color
            binding.appbarSettingSelectedColorImage.imageTintList = ColorStateList.valueOf(color)
        }

        binding.appbarSettingOkButton.setOnClickListener {
            sharedPreferenceUtils.setAppbarColor(selectedColor)
            finish()
        }

        binding.appbarSettingDefaultButton.setOnClickListener {
            val defaultColor = getColor(R.color.main_background)

            sharedPreferenceUtils.setAppbarColor(defaultColor)
            finish()
        }

        binding.appbarSettingCancelButton.setOnClickListener {
            finish()
        }
    }
}