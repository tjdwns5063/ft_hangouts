package com.example.ft_hangouts.ui.abb_bar_setting

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.ColorInt
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.databinding.ActivityAppBarSettingBinding
import com.example.ft_hangouts.ui.main.MainActivity

class AppBarSettingActivity : AppCompatActivity() {
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
            SharedPreferenceUtils.setAppbarColor(selectedColor)
            finish()
        }

        binding.appbarSettingDefaultButton.setOnClickListener {
            val defaultColor = getColor(R.color.main_background)

            SharedPreferenceUtils.setAppbarColor(defaultColor)
            finish()
        }

        binding.appbarSettingCancelButton.setOnClickListener {
            finish()
        }
    }
}